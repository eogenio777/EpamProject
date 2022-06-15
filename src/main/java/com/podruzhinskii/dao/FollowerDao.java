package main.java.com.podruzhinskii.dao;

import main.java.com.podruzhinskii.domain.Follower;
import main.java.com.podruzhinskii.domain.Periodical;

import java.sql.SQLException;
import java.util.List;

public interface FollowerDao extends Crud<Follower> {

    Boolean isRegistered(String name) throws SQLException;

    Boolean isSubscribed(Follower follower, Periodical periodical) throws SQLException;

    List<Periodical> getFollowerSubscriptions (Follower follower) throws SQLException;

    Boolean subscribe(Follower follower, Periodical periodical) throws SQLException;

    Boolean unSubscribe(Follower follower, Periodical periodical) throws SQLException;

    Follower getByName(String name) throws SQLException;

}
