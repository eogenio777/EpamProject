package main.java.com.podruzhinskii.dao.expansions;

        import main.java.com.podruzhinskii.dao.PeriodicalDao;
        import main.java.com.podruzhinskii.domain.Follower;
        import main.java.com.podruzhinskii.domain.Periodical;

        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.util.ArrayList;
        import java.util.List;


public class PeriodicalDaoExpanded implements PeriodicalDao {
    Connection connection;

    public PeriodicalDaoExpanded(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Periodical get(Long id) throws SQLException {
        Periodical Periodical = new Periodical();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Periodicals WHERE periodical_id=?");
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Periodical.setId(resultSet.getLong("periodical_id"));
            Periodical.setName(resultSet.getString("periodical_name"));
            Periodical.setAbout(resultSet.getString("about"));
        }
        return Periodical;
    }

    @Override
    public List<Periodical> getAll() throws SQLException {
        List<Periodical> Periodicals = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Periodicals");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("periodical_name");
            Long id = resultSet.getLong("periodical_id");
            String about = resultSet.getString("about");
            Periodicals.add(new Periodical(id, name, about));
        }
        return Periodicals;
    }
    @Override
    public Boolean delete(Long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Periodicals WHERE periodical_id=?");
        preparedStatement.setLong(1, id);
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public Boolean create(Periodical Periodical) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Periodicals (periodical_name) VALUES (?)");
        preparedStatement.setString(1, Periodical.getName());
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public Boolean update(Periodical Periodical) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Periodicals SET periodical_name=? WHERE periodical_id=?");
        preparedStatement.setString(1, Periodical.getName());
        preparedStatement.setLong(2, Periodical.getId());
        int updateCount = preparedStatement.executeUpdate();
        return updateCount > 0;
    }

    @Override
    public List<Follower> getSubscribersOfPeriodical(Periodical periodical) throws SQLException {
        List<Follower> followers = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT follower_name, periodical_name FROM followers \n" +
                        "INNER JOIN subscriptions ON followers.follower_id = subscriptions.follower_id\n" +
                        "INNER JOIN periodicals ON periodicals.periodical_id = subscriptions.periodical_id " +
                        "AND periodicals.periodical_id = ?");
        preparedStatement.setLong(1, periodical.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("follower_name");
            Long id = resultSet.getLong("follower_id");
            followers.add(new Follower(id, name));
        }
        return followers;
    }
}
