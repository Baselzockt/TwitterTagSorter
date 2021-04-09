package ch.baselzock.twittertagsorter.handler;

import ch.baselzock.twittertagsorter.converter.Converter;
import ch.baselzock.twittertagsorter.converter.ConverterFactory;
import ch.baselzock.twittertagsorter.converter.ConverterType;
import ch.baselzock.twittertagsorter.model.Tweet;
import ch.baselzock.twittertagsorter.sorter.Sorter;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.List;

public class Handler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);
    private final Converter converter = ConverterFactory.getConverterFor(ConverterType.JSON);
    private final Sorter sorter;
    private final Connection connection;

    public Handler(Sorter sorter, Connection connection) {
        this.sorter = sorter;
        this.connection = connection;
    }

    /**
     * Gets Tweets from a activeMQ queue, sorts them corresponding to tags and then writes them into ActiveMq topics
     *
     * @throws JMSException
     */
    public void Handle() throws JMSException {
        while (true) {
            LOGGER.debug("Start handling message");
            long startTime = System.nanoTime();
            processMessage(connection);
            LOGGER.debug("Handled message. took: {} ms", (startTime - System.nanoTime()) / 1000);
        }
    }

    private void processMessage(Connection connection) throws JMSException {
        final Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

        final MessageConsumer consumer = this.createMessageConsumer(session);
        final Message message = consumer.receive(1000);
        consumer.close();

        if (message == null) {
            return;
        }

        Tweet tweet = getTweetFromMessage(message);
        List<String> tags = sorter.getMatchingTags(tweet.getText());

        tags.forEach(tag -> {
            sendTweetToTopic(tweet, session, tag);
        });

        session.commit();
        session.close();
    }


    private void sendTweetToTopic(Tweet tweet, Session session, String tag) {
        try {
            MessageProducer producer = this.createMessageProducer(session, tag);
            String text = converter.convertToString(tweet);
            if (text != null) {
                TextMessage textMessage = session.createTextMessage(text);
                producer.send(textMessage);
                producer.close();
            }
        } catch (JMSException e) {
            handleJmsException(e, session);
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
        }

        return converter.convertToTweet(json);
    }

    private String getMessageText(ActiveMQBytesMessage message) throws JMSException {
        byte[] byteArr = new byte[(int) message.getBodyLength()];
        message.readBytes(byteArr);
        return new String(byteArr);
    }

    private MessageProducer createMessageProducer(Session session, String tag) throws JMSException {
        Destination tagDestination = session.createTopic(tag);
        LOGGER.debug("Sending message to: {}", tag);
        return session.createProducer(tagDestination);
    }

    private void handleJmsException(JMSException e, Session session) {
        LOGGER.error("Could not send message to topic. ERROR: {}", e.getMessage());
        try {
            session.rollback();
        } catch (JMSException jmsException) {
            LOGGER.error("Could not roll back session throwing Runtime Exception. Error: {}",
                    jmsException.getMessage());
            throw new RuntimeException("Could not roll back session. Error: " + jmsException.getMessage());
        }
    }
}
