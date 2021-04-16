package handler;

import ch.baselzock.twittertagsorter.handler.Handler;
import ch.baselzock.twittertagsorter.helper.PooledConnectionHelper;
import ch.baselzock.twittertagsorter.sorter.Sorter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

import javax.jms.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HandlerTest {
    @Test
    @Order(1)
    void testHandler() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final PooledConnectionFactory pooledConnectionFactory =
                PooledConnectionHelper.createPooledConnectionFactory(connectionFactory);
        final Connection connection = pooledConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        String tweet = "{\"coordinates\":null," +
                "\"created_at\":\"Fri Apr 16 06:24:49 +0000 2021\"," +
                "\"current_user_retweet\":null," +
                "\"entities\":" +
                "{\"hashtags\":[]," +
                "\"media\":null," +
                "\"urls\":[]," +
                "\"user_mentions\"" +
                ":[{\"indices\":[0,8]," +
                "\"id\":889754005,\"id_str\":\"889754005\"," +
                "\"name\":\"☕die Ratlose\",\"screen_name\":\"ranke09\"}," +
                "{\"indices\":[9,23],\"id\":1350905394395275270," +
                "\"id_str\":\"1350905394395275270\",\"name\":\"Flunkernatsleiter\"," +
                "\"screen_name\":\"kryptoschland\"},{\"indices\":[24,27],\"id\":114508061,\"id_str\":\"114508061\"," +
                "\"name\":\"Süddeutsche Zeitung\",\"screen_name\":\"SZ\"}]},\"favorite_count\":0," +
                "\"favorited\":false,\"filter_level\":\"low\",\"id\":1382943039266623490," +
                "\"id_str\":\"1382943039266623490\",\"in_reply_to_screen_name\":\"ranke09\"," +
                "\"in_reply_to_status_id\":1382941338199527427,\"in_reply_to_status_id_str\":\"1382941338199527427\"," +
                "\"in_reply_to_user_id\":889754005,\"in_reply_to_user_id_str\":\"889754005\",\"lang\":\"de\"," +
                "\"possibly_sensitive\":false,\"quote_count\":0,\"reply_count\":0,\"retweet_count\":0," +
                "\"retweeted\":false,\"retweeted_status\":null," +
                "\"source\":\"\\u003ca href=\\\"http://twitter.com/download/android\\\" rel=\\\"nofollow\\\"\\u003eTwitter for Android\\u003c/a\\u003e\"," +
                "\"scopes\":null," +
                "\"text\":\"@ranke09 @kryptoschland @SZ  #tree Das RKI zählt als Corona-Todesfälle alle Menschen, die mit einer COVID-19-Erkrankung in Verbindung stehen.\"," +
                "\"full_text\":\"\",\"display_text_range\":[28,134],\"place\":null,\"truncated\":false," +
                "\"user\":{\"contributors_enabled\":false,\"created_at\":\"Sun Apr 21 20:01:05 +0000 2013\"," +
                "\"default_profile\":true,\"default_profile_image\":false,\"description\":\"Kulturchrist, " +
                "politisches Oxymoron, Pazifist, Zocker, freiheitsliebend, parteilos, talentfrei und privat hier. Brainstorminator \uD83D\uDE0E\"," +
                "\"email\":\"\",\"entities\":null,\"favourites_count\":8559,\"follow_request_sent\":false,\"following\":false," +
                "\"followers_count\":48,\"friends_count\":147,\"geo_enabled\":false,\"id\":1370466199,\"id_str\":\"1370466199\"," +
                "\"is_translator\":false,\"lang\":\"\",\"listed_count\":5,\"location\":\"\",\"name\":\"Je_Cruz\",\"notifications\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_tile\":false,\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/1370466199/1590139638\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/1272020926222209025/ZL9XmuQz_normal.jpg\",\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/1272020926222209025/ZL9XmuQz_normal.jpg\",\"profile_link_color\":\"1DA1F2\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"protected\":false,\"screen_name\":\"je_cruz1\",\"show_all_inline_media\":false,\"status\":null,\"statuses_count\":2267,\"time_zone\":\"\",\"url\":\"\",\"utc_offset\":0,\"verified\":false,\"withheld_in_countries\":[],\"withheld_scope\":\"\"},\"withheld_copyright\":false,\"withheld_in_countries\":null,\"withheld_scope\":\"\",\"extended_entities\":null,\"extended_tweet\":null,\"quoted_status_id\":0,\"quoted_status_id_str\":\"\",\"quoted_status\":null}\n";

        Destination destination = session.createQueue("Twitter");
        MessageProducer producer = session.createProducer(destination);

        TextMessage message = session.createTextMessage(tweet);
        producer.send(message);
        producer.close();
        session.commit();


        Sorter sorter = new Sorter();

        Handler handler = new Handler(sorter, connection);
        handler.setTest(true);

        handler.Handle();

        Destination receiveDestination = session.createQueue("#tree");
        MessageConsumer consumer = session.createConsumer(receiveDestination);
        Message msg = consumer.receive(1000);
        assertNotNull(msg);
        if (msg instanceof ActiveMQTextMessage temp) {
            assertTrue(temp.getText().contains("#tree"));
        }
        session.commit();
        session.close();
        connection.close();
    }


    @Test
    @Order(3)
    void testNullTweet() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final PooledConnectionFactory pooledConnectionFactory =
                PooledConnectionHelper.createPooledConnectionFactory(connectionFactory);
        final Connection connection = pooledConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        Destination destination = session.createQueue("Twitter");
        MessageProducer producer = session.createProducer(destination);

        BytesMessage message = session.createBytesMessage();
        producer.send(message);
        producer.close();
        session.commit();


        Sorter sorter = new Sorter();

        Handler handler = new Handler(sorter, connection);
        handler.setTest(true);

        handler.Handle();

        Destination receiveDestination = session.createQueue("#tree");
        MessageConsumer consumer = session.createConsumer(receiveDestination);
        Message msg = consumer.receive(1000);
        assertNull(msg);
        session.commit();
        session.close();
        connection.close();
    }

    @Test
    @Order(2)
    void testNoMessage() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        final PooledConnectionFactory pooledConnectionFactory =
                PooledConnectionHelper.createPooledConnectionFactory(connectionFactory);
        final Connection connection = pooledConnectionFactory.createConnection();
        connection.start();

        Sorter sorter = new Sorter();

        Handler handler = new Handler(sorter, connection);
        handler.setTest(true);

        handler.Handle();

        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

        Destination receiveDestination = session.createQueue("tree");
        MessageConsumer consumer = session.createConsumer(receiveDestination);
        Message msg = consumer.receive(1000);
        assertNull(msg);
        session.commit();
        session.close();
        connection.close();
    }

}
