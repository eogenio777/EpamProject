package test.java.com.podruzhinskii;

import main.java.com.podruzhinskii.domain.Periodical;
import main.java.com.podruzhinskii.service.PeriodicalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import test.resources.ConnectionInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PeriodicalServiceTest {

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
    public void createPeriodical() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Periodical periodical = new Periodical(1L, "IzdanieNomerOdin", "Something about it");
            PeriodicalService periodicalService = new PeriodicalService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM periodicals WHERE periodical_id=5",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            assertEquals(0, getResultSetSize(rs));

            periodicalService.createPeriodical(periodical);
            rs = preparedStatement.executeQuery();
            assertEquals(1, getResultSetSize(rs));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void deletePeriodical() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Periodical periodical = new Periodical();
            periodical.setId(1L);
            PeriodicalService periodicalService = new PeriodicalService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM periodicals WHERE periodical_id=1",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();

            assertEquals(1, getResultSetSize(rs));

            periodicalService.deletePeriodical(periodical);
            rs = preparedStatement.executeQuery();
            assertEquals(0, getResultSetSize(rs));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println(throwables.getMessage());
            fail();
        }
    }

    @Test
    public void updatePeriodical() {
        try (final Connection connection = DriverManager.getConnection(ConnectionInfo.DB_URL, ConnectionInfo.USER, ConnectionInfo.PASS)) {
            Periodical periodical = new Periodical(1L, "IzdanieNomerOdin", "Blank");
            PeriodicalService periodicalService = new PeriodicalService(connection);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM periodicals WHERE periodical_id=1",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = preparedStatement.executeQuery();
            List<Periodical> periodicals = getPeriodicals(rs);
            assertEquals("News Of Saint-Petersburg", periodicals.get(0).getName());

            periodicalService.updatePeriodical(periodical);
            rs = preparedStatement.executeQuery();
            periodicals = getPeriodicals(rs);
            assertEquals("IzdanieNomerOdin", periodicals.get(0).getName());

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

    private List<Periodical> getPeriodicals(ResultSet rs) throws SQLException {
        List<Periodical> periodicals = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("periodical_name");
            Long id = rs.getLong("periodical_id");
            String about = rs.getString("about");
            periodicals.add(new Periodical(id, name, about));
        }
        rs.beforeFirst();
        return periodicals;
    }
}