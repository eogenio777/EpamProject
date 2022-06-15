package main.java.com.podruzhinskii.dao.expansions;

import main.java.com.podruzhinskii.dao.FollowerDao;
import main.java.com.podruzhinskii.domain.Follower;
import main.java.com.podruzhinskii.domain.Periodical;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class FollowerDaoExpanded implements FollowerDao {
    Connection connection;

    public FollowerDaoExpanded(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Follower get(Long id) throws SQLException {
        Follower follower = new Follower();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM followers WHERE follower_id=?");
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            follower.setName(resultSet.getString("follower_name"));
            follower.setId(resultSet.getLong("follower_id"));
        }
        return follower;
    }

    @Override
    public List<Follower> getAll() throws SQLException {
        List<Follower> followers = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM followers");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("follower_name");
            Long id = resultSet.getLong("follower_id");
            followers.add(new Follower(id, name));
        }
        return followers;
    }


    @Override
    public Boolean create(Follower follower) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO followers (follower_name) VALUES (?)");
        preparedStatement.setString(1, follower.getName());
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public Boolean update(Follower follower) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE followers SET follower_name=? WHERE follower_id=?");
        preparedStatement.setString(1, follower.getName());
        preparedStatement.setLong(2, follower.getId());
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public Boolean delete(Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM followers WHERE follower_id=?");
        preparedStatement.setLong(1, id);
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public Boolean isRegistered(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM followers WHERE follower_name=?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    @Override
    public List<Periodical> getFollowerSubscriptions(Follower follower) throws SQLException {
        List<Periodical> followers = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM periodicals \n" +
                        "INNER JOIN subscriptions ON subscriptions.periodical_id = periodicals.periodical_id \n" +
                        "INNER JOIN followers ON followers.follower_id = subscriptions.follower_id \n" +
                        "AND followers.follower_name = ?");
        preparedStatement.setString(1, follower.getName());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("periodical_name");
            Long id = resultSet.getLong("periodical_id");
            String about = resultSet.getString("about");
            followers.add(new Periodical(id, name, about));
        }
        return followers;
    }

    @Override
    public Boolean subscribe(Follower follower, Periodical periodical) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO subscriptions(follower_id, periodical_id) VALUES (?,?)"
        );
        preparedStatement.setLong(1, follower.getId());
        preparedStatement.setLong(2, periodical.getId());
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public Boolean unSubscribe(Follower follower, Periodical periodical) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM subscriptions WHERE follower_id=? AND periodical_id=?");
        preparedStatement.setLong(1, follower.getId());
        preparedStatement.setLong(2, periodical.getId());
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public Follower getByName(String name) throws SQLException {
        Follower follower = new Follower();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM followers WHERE follower_name=?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            follower.setName(resultSet.getString("follower_name"));
            follower.setId(resultSet.getLong("follower_id"));
        }
        return follower;
    }

    @Override
    public Boolean isSubscribed(Follower follower, Periodical periodical) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM subscriptions WHERE follower_id=? AND periodical_id=?"
        );
        preparedStatement.setLong(1, follower.getId());
        preparedStatement.setLong(2, periodical.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
}
