package ch.baselzock.twittertagsorter.helper;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;


public class PooledConnectionHelper {

    private static final int MAXCONNECTIONS = 10;

    public static PooledConnectionFactory
    createPooledConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        final PooledConnectionFactory pooledConnectionFactory =
                new org.apache.activemq.pool.PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory);
        pooledConnectionFactory.setMaxConnections(MAXCONNECTIONS);
        return pooledConnectionFactory;
    }
}
