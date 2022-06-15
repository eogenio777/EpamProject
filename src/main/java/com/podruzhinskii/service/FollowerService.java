package main.java.com.podruzhinskii.service;

import main.java.com.podruzhinskii.dao.FollowerDao;
import main.java.com.podruzhinskii.dao.expansions.FollowerDaoExpanded;
import main.java.com.podruzhinskii.domain.Follower;
import main.java.com.podruzhinskii.domain.Periodical;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FollowerService {
    private final FollowerDao followerDao;

    public FollowerService(Connection connection) {
        this.followerDao = new FollowerDaoExpanded(connection);
    }

    public void showAll() throws SQLException {
        List<Follower> followers = followerDao.getAll();
        if (followers.isEmpty()) {
            System.out.println("Oops, looks like database doesn't have any followers right now");
        } else {
            System.out.println("List of all followers:");
            for (Follower follower : followers) {
                System.out.println("Id: " + follower.getId() + ", Name: " + follower.getName());
            }
        }
    }

    public Boolean isRegistered(Follower follower) throws SQLException {
        return followerDao.isRegistered(follower.getName());
    }

    public void registerFollower(Follower follower) throws SQLException {
        followerDao.create(follower);
    }

    public void showFollowerSubscriptions(Follower follower) throws SQLException {
        List<Periodical> periodicals = followerDao.getFollowerSubscriptions(follower);
        if (periodicals.isEmpty()) {
            System.out.println("Oops, looks like this follower don't have any subscriptions right now");
        } else {
            System.out.println("List of follower's subscriptions:");
            for (Periodical periodical : periodicals) {
                System.out.println("\nId: " + periodical.getId() +
                        ", Name: " + periodical.getName() +
                        "\nAbout: " + periodical.getAbout());
            }
        }
    }

    public void deleteFollower(Follower follower) throws SQLException {
        followerDao.delete(follower.getId());
    }

    // in case was just registered
    public Long getFollowerId(Follower follower) throws SQLException {
        return followerDao.getByName(follower.getName()).getId();
    }

    public void showFollower(Follower follower) throws SQLException {
        Follower res = followerDao.get(follower.getId());
        System.out.println("Id: " + res.getId() + ", Name: " + res.getName());
    }

    public void updateFollower(Follower follower) throws SQLException {
        followerDao.update(follower);
    }

    public Boolean isSubscribed(Follower follower, Periodical periodical) throws SQLException {
        return followerDao.isSubscribed(follower, periodical);
    }

    public void subscribe(Follower follower, Periodical periodical) throws SQLException {
        followerDao.subscribe(follower, periodical);
    }

    public void unSubscribe(Follower follower, Periodical periodical) throws SQLException {
        followerDao.unSubscribe(follower, periodical);
    }
}
