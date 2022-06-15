package main.java.com.podruzhinskii.dao;

import main.java.com.podruzhinskii.domain.Follower;
import main.java.com.podruzhinskii.domain.Periodical;

import java.sql.SQLException;
import java.util.List;

public interface PeriodicalDao extends Crud<Periodical> {
    List<Follower> getSubscribersOfPeriodical(Periodical periodical) throws SQLException;

}
