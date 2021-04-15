package handler;

import ch.baselzock.twittertagsorter.converter.Converter;
import ch.baselzock.twittertagsorter.converter.ConverterFactory;
import ch.baselzock.twittertagsorter.converter.ConverterType;
import ch.baselzock.twittertagsorter.handler.Handler;
import ch.baselzock.twittertagsorter.model.Tweet;
import ch.baselzock.twittertagsorter.sorter.Sorter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import javax.jms.*;

public class HandlerTest {

    @Test
    void testHandler() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        Tweet tweet = new Tweet();
        tweet.setText("#tree");
        Destination destination = session.createQueue("Twitter");
        MessageProducer producer = session.createProducer(destination);
        Converter converter = ConverterFactory.getConverterFor(ConverterType.JSON);
        TextMessage message = session.createTextMessage(converter.convertToString(tweet));
        producer.send(message);
        producer.close();
        session.commit();


        Sorter sorter = new Sorter();

        Handler handler = new Handler(sorter, connection);
        handler.setTest(true);

        handler.Handle();
        destination = session.createTopic("tree");
        MessageConsumer consumer = session.createConsumer(destination);
        Message msg = consumer.receive(1000);
        assertNotNull(msg);
        if (msg instanceof ActiveMQTextMessage temp) {
            Tweet tempTweet = converter.convertToTweet(temp.getText());
            assertEquals("#tree", tempTweet.getText());
        }

        connection.close();
    }
}
