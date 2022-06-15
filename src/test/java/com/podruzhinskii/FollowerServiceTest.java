package test.java.com.podruzhinskii;

import main.java.com.podruzhinskii.domain.Follower;
import main.java.com.podruzhinskii.domain.Periodical;
import main.java.com.podruzhinskii.service.FollowerService;
import org.junit.jupiter.api.*;
import test.resources.ConnectionInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FollowerServiceTest {

    @BeforeEach
    void init() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DROP TABLE IF EXISTS subscriptions;\n" +
                            "DROP TABLE IF EXISTS followers;\n" +
                            "DROP TABLE IF EXISTS periodicals;")) {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "-- читатели\n" +
                            "CREATE TABLE followers\n" +
                            "(\n" +
                            "    follower_id SERIAL primary key ,\n" +
                            "    follower_name VARCHAR(50) not null\n" +
                            ");\n" +
                            "\n" +
                            "-- издания\n" +
                            "CREATE TABLE periodicals\n" +
                            "(\n" +
                            "    periodical_id SERIAL primary key,\n" +
                            "    periodical_name VARCHAR(50) not null,\n" +
                            "\tabout VARCHAR(500)\n" +
                            ");\n" +
                            "\n" +
                            "\n" +
                            "-- подписки - читатель-издание\n" +
                            "CREATE TABLE subscriptions\n" +
                            "(\n" +
                            "\tfollower_id INTEGER REFERENCES followers(follower_id) ON DELETE CASCADE,\n" +
                            "\tperiodical_id INTEGER REFERENCES periodicals(periodical_id) ON DELETE CASCADE\n" +
                            ");"
            )) {
                preparedStatement.executeUpdate();

            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO followers(follower_name) VALUES ('Evgenii'),('Vasya'),('John'),('Albert'),('Vova'),('Masha'),('xXx_Killer_xXx');\n" +
                            "\n" +
                            "INSERT INTO periodicals(periodical_name, about) VALUES\n" +
                            "('News Of Saint-Petersburg',\n" +
                            "\t E'Every day news of SP city, such as culture, politics, city events and many others.\\nYou won\\'t miss anything with \\'News Of Saint-Petersburg\\''),\n" +
                            "\n" +
                            "\t('Fashion Trends',\n" +
                            "\t E'All about modern fashion industry'),\n" +
                            "\n" +
                            "\t('Movie Theatre Online',\n" +
                            "\t E'Reviews of all new (and not only) films, series and cartoons. Each day at least one new post!'),\n" +
                            "\n" +
                            "\t ('Music',\n" +
                            " \t  E'Listen to music, and read about it too!');\n"
            )) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            Assertions.fail();
            e.printStackTrace();
        }
    }

    @AfterAll
    static void dropTables() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DROP TABLE IF EXISTS subscriptions;\n" +
                            "DROP TABLE IF EXISTS followers;\n" +
                            "DROP TABLE IF EXISTS periodicals;")) {
                preparedStatement.executeUpdate();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

        @Test
    public void isRegistered() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower();

            follower.setName("Vasya");
            FollowerService followerService = new FollowerService(connection);
            Boolean vasyaRegisteredMustBeTrue = followerService.isRegistered(follower);
            assertTrue(vasyaRegisteredMustBeTrue);

            follower.setName("Evpatiy");
            Boolean evpatiyRegisteredMustBeFalse = followerService.isRegistered(follower);
            assertFalse(evpatiyRegisteredMustBeFalse);
        } catch (SQLException throwables) {
            fail("SQLexception throw");
            throwables.printStackTrace();
        }
    }

    @Test
    public void registerFollower() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower();
            follower.setName("Evpatiy");
            FollowerService followerService = new FollowerService(connection);

            followerService.registerFollower(follower);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM followers WHERE follower_name='Evpatiy'",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            assertEquals(1, getResultSetSize(rs));
            List<Follower> followers = getFollowers(rs);
            Follower expected = new Follower(8L, "Evpatiy");
            assertEquals(expected.getId(), followers.get(0).getId());
            assertEquals(expected.getName(), followers.get(0).getName());

            Boolean evpatiyRegisteredMustBeTrue = followerService.isRegistered(follower);
            assertTrue(evpatiyRegisteredMustBeTrue);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void deleteFollower() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower();
            follower.setId(1L);
            FollowerService followerService = new FollowerService(connection);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM followers WHERE follower_id = 1",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            assertEquals(1, getResultSetSize(rs));

            followerService.deleteFollower(follower);
            rs = preparedStatement.executeQuery();
            assertEquals(0, getResultSetSize(rs));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void getFollowerId() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower();
            follower.setName("Evgenii");
            FollowerService followerService = new FollowerService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM followers WHERE follower_name='Evgenii'",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            List<Follower> followers = getFollowers(rs);
            assertEquals(followers.get(0).getId(), followerService.getFollowerId(follower));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void updateFollower() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower(1L, "Evgen");
            FollowerService followerService = new FollowerService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM followers WHERE follower_id=1",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();
            List<Follower> followers = getFollowers(rs);
            assertEquals("Evgenii", followers.get(0).getName());

            followerService.updateFollower(follower);
            rs = preparedStatement.executeQuery();
            followers = getFollowers(rs);
            assertEquals("Evgen", followers.get(0).getName());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void isSubscribed() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower();
            follower.setId(1L);
            Periodical periodical = new Periodical();
            periodical.setId(1L);
            FollowerService followerService = new FollowerService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM subscriptions",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            assertEquals(0, getResultSetSize(rs));

            Boolean mustBeFalse = followerService.isSubscribed(follower, periodical);
            assertFalse(mustBeFalse);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void subscribe() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower();
            follower.setId(1L);
            Periodical periodical = new Periodical();
            periodical.setId(1L);
            FollowerService followerService = new FollowerService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM subscriptions",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            assertEquals(0, getResultSetSize(rs));

            followerService.subscribe(follower, periodical);
            rs = preparedStatement.executeQuery();
            assertEquals(1, getResultSetSize(rs));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void unSubscribe() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Follower follower = new Follower();
            follower.setId(1L);
            Periodical periodical = new Periodical();
            periodical.setId(1L);
            FollowerService followerService = new FollowerService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM subscriptions",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            assertEquals(0, getResultSetSize(rs));

            followerService.subscribe(follower, periodical);
            rs = preparedStatement.executeQuery();
            assertEquals(1, getResultSetSize(rs));

            followerService.unSubscribe(follower, periodical);
            rs = preparedStatement.executeQuery();
            assertEquals(0, getResultSetSize(rs));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    private int getResultSetSize(ResultSet rs) throws SQLException {
        int size = 0;
        while (rs.next()) {
            size++;
        }
        rs.beforeFirst();
        return size;
    }

    private List<Follower> getFollowers(ResultSet rs) throws SQLException {
        List<Follower> followers = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("follower_name");
            Long id = rs.getLong("follower_id");
            followers.add(new Follower(id, name));
        }
        rs.beforeFirst();
        return followers;
    }
}