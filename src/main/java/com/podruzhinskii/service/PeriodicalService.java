package main.java.com.podruzhinskii.service;

import main.java.com.podruzhinskii.dao.PeriodicalDao;
import main.java.com.podruzhinskii.dao.PeriodicalDao;
import main.java.com.podruzhinskii.dao.expansions.PeriodicalDaoExpanded;
import main.java.com.podruzhinskii.dao.expansions.PeriodicalDaoExpanded;
import main.java.com.podruzhinskii.domain.Follower;
import main.java.com.podruzhinskii.domain.Periodical;
import main.java.com.podruzhinskii.domain.Periodical;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PeriodicalService {
    private PeriodicalDao periodicalDao;

    public PeriodicalService(Connection connection) {
        this.periodicalDao = new PeriodicalDaoExpanded(connection);
    }

    public void showAll() throws SQLException {
        List<Periodical> periodicals = periodicalDao.getAll();
        if (periodicals.isEmpty()) {
            System.out.println("Oops, looks like this follower don't have any subscriptions right now");
        } else {
            System.out.println("List of all periodicals:");
            for (Periodical periodical : periodicals) {
                System.out.println("Id: " + periodical.getId() +
                        ", Name: " + periodical.getName() +
                        "\nAbout: " + periodical.getAbout() +"\n");
            }
        }
    }

    public void createPeriodical(Periodical periodical) throws SQLException {
        periodicalDao.create(periodical);
    }

    public void deletePeriodical(Periodical periodical) throws SQLException {
        periodicalDao.delete(periodical.getId());
    }

    public void showPeriodical(Periodical periodical) throws SQLException {
        Periodical res = periodicalDao.get(periodical.getId());
        System.out.println("\nId: " + res.getId() + ", Name: " + res.getName() +
                "\nAbout: " + res.getAbout());
    }

    public void updatePeriodical(Periodical periodical) throws SQLException {
        periodicalDao.update(periodical);
    }

    public void showSubscribers(Periodical periodical) throws SQLException {
        List<Follower> followers = periodicalDao.getSubscribersOfPeriodical(periodical);
        if (followers.isEmpty()) {
            System.out.println("Oops, looks like nobody have subscribed on this periodical");
        } else {
            System.out.println("List of periodical's followers:");
            for (Follower follower : followers) {
                System.out.println("Id: " + follower.getId() +
                        ", Name: " + follower.getName());
            }
        }

        periodicalDao.getSubscribersOfPeriodical(periodical);
    }
}
