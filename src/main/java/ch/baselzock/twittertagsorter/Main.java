package ch.baselzock.twittertagsorter;

import ch.baselzock.twittertagsorter.sorter.Sorter;
import ch.baselzock.twittertagsorter.helper.PooledConnectionHelper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws JMSException {
        LOGGER.debug("Getting ActiveMQConnectionFactory for endpoint: {}", System.getenv("ENDPOINT"));
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(System.getenv("ENDPOINT"));
        LOGGER.debug("Setting redelivery policy");
        RedeliveryPolicy policy = connectionFactory.getRedeliveryPolicy();
        policy.setInitialRedeliveryDelay(50);
        policy.setBackOffMultiplier(1.5);
        policy.setUseExponentialBackOff(true);
        policy.setMaximumRedeliveries(2);
        connectionFactory.setRedeliveryPolicy(policy);
        LOGGER.debug("Getting pooled Connection Factory");
        final PooledConnectionFactory pooledConnectionFactory =
                PooledConnectionHelper.createPooledConnectionFactory(connectionFactory);
        LOGGER.debug("Getting connection");
        final Connection connection = connectionFactory.createConnection();
        LOGGER.debug("Starting connection");
        connection.start();
        LOGGER.debug("Creating Handler");
        Sorter sorter = new Sorter(connection);
        LOGGER.debug("Start handling tweets from activemq");
        sorter.start();
        connection.close();
    }

}
