package ch.baselzock.twittertagsorter.sorter;

import ch.baselzock.twittertagsorter.converter.Converter;
import ch.baselzock.twittertagsorter.converter.ConverterFactory;
import ch.baselzock.twittertagsorter.converter.ConverterType;
import ch.baselzock.twittertagsorter.exceptions.InvalidMessageException;
import ch.baselzock.twittertagsorter.exceptions.MarshalException;
import ch.baselzock.twittertagsorter.exceptions.UnmarshalException;
import ch.baselzock.twittertagsorter.model.Tweet;
import ch.baselzock.twittertagsorter.tagmatcher.TagMatcher;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.List;

public class Sorter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sorter.class);
    private final Converter converter = ConverterFactory.getConverterFor(ConverterType.JSON);
    private final TagMatcher tagMatcher;
    private final Connection connectionIn;
    private final Connection connectionOut;
    private boolean test;

    public Sorter(Connection connectionIn, Connection connectionOut) {
        this.tagMatcher = new TagMatcher();
        this.connectionIn = connectionIn;
        this.connectionOut = connectionOut;
        this.test = false;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    /**
     * Gets Tweets from a activeMQ queue, sorts them corresponding to tags and then writes them into ActiveMq topics
     */
    public void run() throws JMSException {

        final Session sessionIn = connectionIn.createSession(true, Session.SESSION_TRANSACTED);
        final Session sessionOut = connectionOut.createSession(true, Session.SESSION_TRANSACTED);
        final MessageConsumer consumer = this.createMessageConsumer(sessionIn);

        do {
            LOGGER.debug("Start handling message");
            long startTime = System.nanoTime();
            try {
                processMessage(sessionOut, consumer);
                sessionOut.commit();
                sessionIn.commit();
            } catch (JMSException e) {
                LOGGER.error("Handling message failed");
                LOGGER.error(e.getMessage());

                sessionIn.rollback();
                sessionOut.rollback();
            }
            LOGGER.debug("Handled message. took: {} ms", (startTime - System.nanoTime()) / 1000);
        } while (!test);
        sessionIn.close();
        sessionOut.close();
    }

    private void processMessage(Session sessionOut, MessageConsumer consumer) throws JMSException {
        final long timeout = 1000;
        final Message message = consumer.receive(timeout);

        if (message == null) {
            LOGGER.debug("Message was null");
            return;
        }

        Tweet tweet = null;
        try {
            tweet = getTweetFromMessage(message);
        } catch (InvalidMessageException | UnmarshalException e) {
            LOGGER.error("Invalid message Error: {}", e.getMessage());
            return;
        }

        String text;
        try {
            text = converter.convertToString(tweet);
        } catch (MarshalException e) {
            LOGGER.error("Could not Marshal tweet. Error: {}", e.getMessage());
            return;
        }

        List<String> tags = tagMatcher.getAllTags(tweet.getText());

        LOGGER.debug("Start sending message to activeMQ");
        for (String topic : tags) {
            LOGGER.debug("Sending to topic {}", topic);
            sendTextToTopic(text, sessionOut, topic);
        }
    }

    private MessageConsumer createMessageConsumer(Session session) throws JMSException {
        final Destination consumerDestination = session.createQueue("Twitter");
        return session.createConsumer(consumerDestination);
    }

    private Tweet getTweetFromMessage(Message message) throws JMSException, InvalidMessageException, UnmarshalException {
        String json = getMessageText(message);
        return converter.convertToTweet(json);
    }

    private String getMessageText(Message message) throws JMSException, InvalidMessageException {
        if (message instanceof ActiveMQBytesMessage msg) {
            if (msg.getBodyLength() == 0) {
                throw new InvalidMessageException("Empty message");
            }
            byte[] byteArr = new byte[(int) msg.getBodyLength()];
            msg.readBytes(byteArr);
            return new String(byteArr);
        } else if (message instanceof ActiveMQTextMessage msg) {
            if (msg.getText().isEmpty()) {
                throw new InvalidMessageException("Empty message");
            }
            return msg.getText();
        } else {
            throw new InvalidMessageException("Unknown Message type");
        }
    }

    private void sendTextToTopic(String text, Session session, String topic) throws JMSException {
        MessageProducer producer = this.createMessageProducer(session, topic);
        LOGGER.debug("Sending tweet");
        TextMessage textMessage = session.createTextMessage(text);
        producer.send(textMessage);
        producer.close();
    }

    private MessageProducer createMessageProducer(Session session, String tag) throws JMSException {
        Destination tagDestination;
        if (test) {
            //cannot get message from Topic with embedded broker using queue instead
            tagDestination = session.createQueue(tag);
        } else {
            tagDestination = session.createTopic(tag);
        }

        LOGGER.debug("Sending message to: {}", tag);
        return session.createProducer(tagDestination);
    }
}
