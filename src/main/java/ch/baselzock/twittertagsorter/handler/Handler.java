package ch.baselzock.twittertagsorter.handler;

import ch.baselzock.twittertagsorter.converter.Converter;
import ch.baselzock.twittertagsorter.converter.ConverterFactory;
import ch.baselzock.twittertagsorter.converter.ConverterType;
import ch.baselzock.twittertagsorter.model.Tweet;
import ch.baselzock.twittertagsorter.sorter.Sorter;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.List;

public class Handler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);
    private final Converter converter = ConverterFactory.getConverterFor(ConverterType.JSON);
    private final Sorter sorter;
    private final Connection connection;
    private boolean test;

    public Handler(Sorter sorter, Connection connection) {
        this.sorter = sorter;
        this.connection = connection;
        this.test = false;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    /**
     * Gets Tweets from a activeMQ queue, sorts them corresponding to tags and then writes them into ActiveMq topics
     */
    public void Handle() {
        try {
            final Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            do {
                LOGGER.debug("Start handling message");
                long startTime = System.nanoTime();
                try {
                    processMessage(session);
                } catch (JMSException e) {
                    LOGGER.error("Handling message failed");
                    LOGGER.error(e.getMessage());
                    session.rollback();
                }
                LOGGER.debug("Handled message. took: {} ms", (startTime - System.nanoTime()) / 1000);
            } while (!test);
            session.close();
        } catch (JMSException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void processMessage(Session session) throws JMSException {
        final MessageConsumer consumer = this.createMessageConsumer(session);
        final Message message = consumer.receive(1000);
        consumer.close();

        if (message == null) {
            return;
        }

        Tweet tweet = getTweetFromMessage(message);

        if (tweet == null) {
            LOGGER.debug("Could not get Tweet from message");
            return;
        }

        List<String> tags = sorter.getMatchingTags(tweet.getText());

        for (String tag : tags) {
            sendTweetToTopic(tweet, session, tag);
            session.commit();
        }
        session.commit();
    }


    private void sendTweetToTopic(Tweet tweet, Session session, String tag) throws JMSException {
        MessageProducer producer = this.createMessageProducer(session, tag);
        String text = converter.convertToString(tweet);
        if (text != null) {
            TextMessage textMessage = session.createTextMessage(text);
            producer.send(textMessage);
            producer.close();
        }
    }

    private MessageConsumer createMessageConsumer(Session session) throws JMSException {
        final Destination consumerDestination = session.createQueue("Twitter");
        return session.createConsumer(consumerDestination);
    }

    private Tweet getTweetFromMessage(Message message) throws JMSException {
        String json = "";
        if (message instanceof ActiveMQBytesMessage msg) {
            json = getMessageText(msg);
        } else if (message instanceof ActiveMQTextMessage msg) {
            json = msg.getText();
        }

        return converter.convertToTweet(json);
    }

    private String getMessageText(ActiveMQBytesMessage message) throws JMSException {
        byte[] byteArr = new byte[(int) message.getBodyLength()];
        message.readBytes(byteArr);
        return new String(byteArr);
    }

    private MessageProducer createMessageProducer(Session session, String tag) throws JMSException {
        Destination tagDestination;
        if (test) {
            tagDestination = session.createQueue(tag);
        } else {
            tagDestination = session.createTopic(tag);
        }

        LOGGER.debug("Sending message to: {}", tag);
        return session.createProducer(tagDestination);
    }
}
