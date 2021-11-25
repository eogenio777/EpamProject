package main.java.com.podruzhinskii;

import main.java.com.podruzhinskii.domain.Follower;
import main.java.com.podruzhinskii.domain.Periodical;
import main.java.com.podruzhinskii.service.FollowerService;
import main.java.com.podruzhinskii.service.PeriodicalService;
import test.resources.ConnectionInfo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class UserInteraction {

    private static String DB_URL;
    private static String USER;
    private static String PASS;

    //resources
    private static final String U = "U";
    private static final String SU = "SU";
    private static final String PARTING = "Ok, bye";
    private static final String YES = "Y";
    private static final String NO = "N";

    public UserInteraction() {
        DB_URL = ConnectionInfo.DB_URL;
        USER = ConnectionInfo.USER;
        PASS = ConnectionInfo.PASS;

    }


    Scanner input = new Scanner(System.in);
    FollowerService followerService;
    PeriodicalService periodicalService;


    public void interact() {
//        TODO: написать тесты
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            followerService = new FollowerService(connection);
            periodicalService = new PeriodicalService(connection);
            System.out.println("""
                    This is application helps to manage user's subscriptions, choose mode:\s
                    regular user [U] (can subscribe or unsubscribe)
                    superuser [SU] (can make all CRUD queries to followers and periodicals tables, but can not subscribe)""");
            String response = input.nextLine();
            if (response.equals(U)) {
                userInteraction();
            } else if (response.equals(SU)) {
                superUserInteraction();
            }

            followerService.showAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void userInteraction() throws SQLException {
        Follower newFollower = new Follower();
        System.out.println("Hello, User!\nPlease type your name: ");
        String name = input.nextLine();
        newFollower.setName(name);
        String permission;
        if (followerService.isRegistered(newFollower)) {
            System.out.println("Hello, " + newFollower.getName() +
                    ", you are already in the database, want to see menu? [Y/N]");
        } else {
            System.out.println("You are not in the database, want to register? [Y/N]");
            permission = input.nextLine();
            if (permission.equals(YES)) {
                followerService.registerFollower(newFollower);
            } else if (permission.equals(NO)) {
                System.out.println(PARTING);
                System.exit(0);
            }
            System.out.println("You are now registered, do you want to see menu? [Y/N]");
        }
        newFollower.setId(followerService.getFollowerId(newFollower));
        permission = input.nextLine();
        if (permission.equals(YES)) {
            userMenu(newFollower);
        } else if (permission.equals(NO)) {
            System.out.println(PARTING);
            System.exit(0);
        } else {
            System.out.println("Unexpected input, bye");
            System.exit(0);
        }
    }

    private void userMenu(Follower follower) throws SQLException {
        while (true) {
            System.out.println("""

                    What you would like to do? Type number:
                    1)Get all periodicals
                    2)Get a specific periodical (id, name and information about it)
                    3)Get all your subscriptions
                    4)Subscribe to a specific periodical
                    5)Unsubscribe from a specific periodical
                    6)Exit program                
                    """);

            String choice = input.nextLine();

            Periodical tempPeriodical = new Periodical();

            switch (choice) {
                case "1" -> periodicalService.showAll();
                case "2" -> {
                    System.out.println("Type id of the periodical you would like to get");
                    tempPeriodical.setId(input.nextLong());
                    input.nextLine();
                    periodicalService.showPeriodical(tempPeriodical);
                }
                case "3" -> followerService.showFollowerSubscriptions(follower);
                case "4" -> {
                    System.out.println("Type id of the periodical you would like to subscribe to");
                    tempPeriodical.setId(input.nextLong());
                    input.nextLine();
                    if (followerService.isSubscribed(follower, tempPeriodical)) {
                        System.out.println("You are already subscriber of this periodical");
                    } else {
                        followerService.subscribe(follower, tempPeriodical);
                    }
                }
                case "5" -> {
                    System.out.println("Type id of the periodical you would like to unsubscribe from");
                    tempPeriodical.setId(input.nextLong());
                    input.nextLine();
                    if (!followerService.isSubscribed(follower, tempPeriodical)) {
                        System.out.println("You have no such subscription");
                    } else {
                        followerService.unSubscribe(follower, tempPeriodical);
                    }
                }
                case "6" -> {
                    System.out.println(PARTING);
                    System.exit(0);
                }
            }
            int resp = showMenuDialog(U);
            if (resp == 0 || resp == -1) {
                System.out.println(PARTING);
                System.exit(0);
            }
        }
    }

    private void superUserInteraction() throws SQLException {
        while (true) {
            System.out.println("""

                    What you would like to do? Type number:
                    1)Get all followers
                    2)Get a specific follower (name and id)
                    3)Create a follower
                    4)Update info about specific follower
                    5)Delete specific follower
                    6)Get all subscriptions of specific follower
                    7)Get all periodicals
                    8)Get a specific periodical (id, name and information about it)
                    9)Create a periodical
                    10)Update info about specific periodical
                    11)Delete specific periodical
                    12)Get list of subscribers for a specific periodical
                    13)Exit program
                    """);

            String choice = input.nextLine();

            Follower tempFollower = new Follower();
            Periodical tempPeriodical = new Periodical();

            switch (choice) {
                case "1" -> followerService.showAll();
                case "2" -> {
                    System.out.println("Type id of the follower you would like to get");
                    tempFollower.setId(input.nextLong());
                    input.nextLine();
                    followerService.showFollower(tempFollower);
                }
                case "3" -> {
                    System.out.println("Type name of new follower");
                    tempFollower.setName(input.nextLine());
                    followerService.registerFollower(tempFollower);
                }
                case "4" -> {
                    System.out.println("Type id of the follower you would like to update");
                    tempFollower.setId(input.nextLong());
                    input.nextLine();
                    System.out.println("Type new name of the follower");
                    tempFollower.setName(input.nextLine());
                    followerService.updateFollower(tempFollower);
                }
                case "5" -> {
                    System.out.println("Type id of the follower you would like to delete");
                    tempFollower.setId(input.nextLong());
                    input.nextLine();
                    followerService.deleteFollower(tempFollower);
                }
                case "6" -> {
                    System.out.println("Type name of the follower to see their subscriptions");
                    tempFollower.setName(input.nextLine());
                    followerService.showFollowerSubscriptions(tempFollower);
                }
                case "7" -> periodicalService.showAll();
                case "8" -> {
                    System.out.println("Type id of the periodical you would like to get");
                    tempPeriodical.setId(input.nextLong());
                    input.nextLine();
                    periodicalService.showPeriodical(tempPeriodical);
                }
                case "9" -> {
                    System.out.println("Type name of new periodical");
                    tempPeriodical.setName(input.nextLine());
                    System.out.println("Type what is new periodical about");
                    tempPeriodical.setAbout(input.nextLine());
                    periodicalService.createPeriodical(tempPeriodical);
                }
                case "10" -> {
                    System.out.println("Type id of the periodical you would like to update");
                    tempPeriodical.setId(input.nextLong());
                    input.nextLine();
                    System.out.println("Type new name of the periodical");
                    tempPeriodical.setName(input.nextLine());
                    System.out.println("Type new info about the periodical");
                    tempPeriodical.setAbout(input.nextLine());
                    periodicalService.updatePeriodical(tempPeriodical);
                }
                case "11" -> {
                    System.out.println("Type id of the periodical you would like to delete");
                    tempPeriodical.setId(input.nextLong());
                    input.nextLine();
                    periodicalService.deletePeriodical(tempPeriodical);
                }
                case "12" -> {
                    System.out.println("Type id of the periodical to see all subscribers");
                    tempPeriodical.setId(input.nextLong());
                    input.nextLine();
                    periodicalService.showSubscribers(tempPeriodical);
                }
                case "13" -> {
                    System.out.println(PARTING);
                    System.exit(0);
                }
            }
            int resp = showMenuDialog(SU);
            if (resp == 0 || resp == -1) {
                System.out.println(PARTING);
                System.exit(0);
            }
        }
    }

    private int showMenuDialog(String userType) {
        //return 1 - YES
        //return 0 - NO
        //return -1 - unexpected input
        System.out.println("\nShow [" + userType + "] menu?[Y/N]\n");
        String choice = input.nextLine();
        if (choice.equals(YES)) {
            return 1;
        } else if (choice.equals(NO)) {
            return 0;
        } else {
            return -1;
        }
    }
}
