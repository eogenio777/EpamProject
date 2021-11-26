package main.java.com.podruzhinskii;

public class Main {
    public static void main(String[] args) {
        //TODO: нужно вынести удаление и инициализацию бд в отдельную хуйню, чтоб все было по красоте
        UserInteraction interaction = new UserInteraction();
        interaction.interact();
    }
}
