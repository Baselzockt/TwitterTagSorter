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

    public static void main(String[] args) {
        Connection connectionIn = null;
        Connection connectionOut = null;
        String endpointIn = System.getenv("ENDPOINTIN");
        String endpointOut = System.getenv("ENDPOINTOUT");

        try {
            connectionOut = Main.setupConnection(endpointOut);
            LOGGER.debug("Starting connection");
            connectionOut.start();
        } catch (JMSException e) {
            LOGGER.error("Could not initialize ActiveMQ connection. Endpoint out: {} ERROR: {}", endpointOut, e.getMessage());
            System.exit(-1);
        }

        try {
            connectionIn = Main.setupConnection(endpointIn);
            LOGGER.debug("Starting connection");
            connectionIn.start();
        } catch (JMSException e) {
            LOGGER.error("Could not initialize ActiveMQ connection.Endpoint in: {} ERROR: {}", endpointIn, e.getMessage());
            System.exit(-1);
        }

        LOGGER.debug("Creating Handler");
        Sorter sorter = new Sorter(connectionIn, connectionOut);
        LOGGER.debug("Start handling tweets from activemq");

        try {
            sorter.run();
        } catch (JMSException e) {
            LOGGER.error("Error processing messages. Error: {}", e.getMessage());
        }

        try {
            connectionOut.close();
            connectionIn.close();
        } catch (JMSException e) {
            LOGGER.error("Could not close ActiveMQ connection. Error: {}", e.getMessage());
        }
    }

    public static Connection setupConnection(String endpoint) throws JMSException {
        LOGGER.debug("Getting ActiveMQConnectionFactory for endpoint: {}", endpoint);
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(endpoint);
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
        return pooledConnectionFactory.createConnection();
    }
}
