package ch.baselzock.twittertagsorter;

import ch.baselzock.twittertagsorter.handler.Handler;
import ch.baselzock.twittertagsorter.helper.PooledConnectionHelper;
import ch.baselzock.twittertagsorter.sorter.Sorter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Arrays;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws JMSException {
        LOGGER.debug("Getting ActiveMQConnectionFactory for endpoint: {}", System.getenv("ENDPOINT"));
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(System.getenv("ENDPOINT"));
        LOGGER.debug("Getting pooled Connection Factory");
        final PooledConnectionFactory pooledConnectionFactory =
                PooledConnectionHelper.createPooledConnectionFactory(connectionFactory);
        LOGGER.debug("Getting connection");
        final Connection connection = connectionFactory.createConnection();
        LOGGER.debug("Starting connection");
        connection.start();
        LOGGER.debug("Creating Sorter");
        Sorter sorter = new Sorter();
        LOGGER.debug("Adding tags");
        String[] tags = System.getenv("TAGS").split(",");
        sorter.setTags(Arrays.stream(tags).toList());
        LOGGER.debug("Added {} tags", tags.length);
        LOGGER.debug("Creating Handler");
        Handler handler = new Handler(sorter, connection);
        LOGGER.debug("Start handling tweets from activemq");
        handler.Handle();
        connection.close();
    }

}
