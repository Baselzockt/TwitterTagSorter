package ch.baselzock.twittertagsorter;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.jms.pool.PooledConnectionFactory;

import javax.jms.*;

public class Main {

    public static void main(String[] args) throws JMSException {
        final ActiveMQConnectionFactory connectionFactory = getActiveMqFactory();
        final PooledConnectionFactory pooledConnectionFactory =
                createPooledConnectionFactory(connectionFactory);
        final Connection connection = connectionFactory.createConnection();
        connection.start();
        while (true) {
            final Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            final Destination consumerDestination = session.createQueue("Twitter");
            final MessageConsumer consumer = session.createConsumer(consumerDestination);
            final Message message = consumer.receive(1000);

            if (message == null) {
                continue;
            }

            if (message instanceof TextMessage msg) {
                String json = msg.getText();
            }

        }
    }

    private static ActiveMQConnectionFactory getActiveMqFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(System.getenv("ENDPOINT"));
        RedeliveryPolicy policy = connectionFactory.getRedeliveryPolicy();
        policy.setInitialRedeliveryDelay(50);
        policy.setBackOffMultiplier(1);
        policy.setUseExponentialBackOff(true);
        policy.setMaximumRedeliveries(1);
        connectionFactory.setRedeliveryPolicy(policy);
        return connectionFactory;
    }

    private static PooledConnectionFactory
    createPooledConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        final PooledConnectionFactory pooledConnectionFactory =
                new org.apache.activemq.pool.PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory);
        pooledConnectionFactory.setMaxConnections(10);
        return pooledConnectionFactory;
    }

}
