package monitoring.service.dev;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.controllers.AuthController;
import monitoring.service.dev.dtos.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;

import java.util.Scanner;

public class MonitoringServiceCLI {
    private static final Scanner keyboard = new Scanner(System.in);
    private static final AuthController auth = AuthController.getInstance();

    public static void main(String[] args) {
        showAvailableCommands();

        String action;
        while (true) {
            System.out.print("> ");
            action = keyboard.nextLine();

            switch (action.toLowerCase()) {
                case "/register" ->{
                    Person person = handleRegistration();
                    handleSession(person);
                }
                case "/login" ->{
                    Person person = handleLogin();
                    handleSession(person);
                }
                case "/exit" ->{
                    System.out.println("Exiting program...");
                    return;
                }
                default -> System.out.println("Unknown command. Type '/help' for available commands.");
            }
        }
    }

    private static Person handleRegistration() {
        System.out.println("To register, you need to come up with a username and password.");
        while (true) {
            try {
                System.out.print("Username: ");
                String username = keyboard.nextLine();
                if (back(username)) break;

                System.out.print("Password: ");
                String password = keyboard.nextLine();
                if (back(password)) break;

                CredentialsDTO dto = CredentialsDTO.builder()
                        .username(username)
                        .password(password)
                        .build();

                Person person = auth.registration(dto);

                System.out.println("Successful registration!"+
                        "\nYou are ~ " + person.getUsername() +" ~ and your role is " + person.getRole());

                return person;
            } catch (NotValidException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    private static Person handleLogin() {
        System.out.println("To authenticate, you need to enter username and password.");
        while (true) {
            try {
                System.out.print("Username: ");
                String username = keyboard.nextLine();
                if (back(username)) break;

                System.out.print("Password: ");
                String password = keyboard.nextLine();
                if (back(password)) break;

                CredentialsDTO dto = CredentialsDTO.builder()
                        .username(username)
                        .password(password)
                        .build();

                Person person = auth.authentication(dto);
                System.out.println("Successful authentication!" +
                        "\nYou are ~ " + person.getUsername() +" ~ and your role is " + person.getRole());

                return person;
            } catch (NotFoundException | NotValidException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    private static void handleSession(Person person){
        Role role = person.getRole();

        if (role == Role.USER) {
            showUserMenu();
        } else if (role == Role.ADMIN) {
            showAdminMenu();
        }

        boolean isSessionActive = true;
        while (isSessionActive) {

            System.out.print("> ");
            String command = keyboard.nextLine();

            switch (command) {
                case "/submitReading":
                    // Логика подачи показаний
                    break;
                case "/getCurrentReadings":
                    // Логика получения актуальных показаний
                    break;
                case "/getMonthlyReadings":
                    // Логика получения показаний за определенный месяц
                    break;
                case "/viewHistory":
                    if (role == Role.ADMIN) {
                        // Логика просмотра истории подачи показаний
                    } else {
                        System.out.println("403 FORBIDDEN (Unauthorized action)");
                    }
                    break;
                case "/userRightsControl":
                    if (role == Role.ADMIN) {
                        // Логика контроля прав пользователя
                    } else {
                        System.out.println("403 FORBIDDEN (Unauthorized action)");
                    }
                    break;
                case "/audit":
                    if (role == Role.ADMIN) {
                        // Логика аудита действий пользователя
                    } else {
                        System.out.println("403 FORBIDDEN (Unauthorized action)");
                    }
                    break;
                case "/logout":
                    isSessionActive = false;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }
    }

    private static boolean back(String string) {
        if ("/back".equalsIgnoreCase(string)) {
            System.out.println("Action canceled.");
            return true;
        }
        return false;
    }

    private static void showAvailableCommands() {
        System.out.println("""
                                 _       __     __                             __                       \s
                                | |     / /__  / /________  ____ ___  ___     / /_____                  \s
                                | | /| / / _ \\/ / ___/ __ \\/ __ `__ \\/ _ \\   / __/ __ \\                 \s
                                | |/ |/ /  __/ / /__/ /_/ / / / / / /  __/  / /_/ /_/ /                 \s
                    __  ___     |__/|__/\\___/_/\\___/\\____/_/ /_/ /_/\\___/___\\__/\\____/         _        \s
                   /  |/  /___  ____  (_) /_____  _____(_)___  ____ _   / ___/___  ______   __(_)_______\s
                  / /|_/ / __ \\/ __ \\/ / __/ __ \\/ ___/ / __ \\/ __ `/   \\__ \\/ _ \\/ ___/ | / / / ___/ _ \\
                 / /  / / /_/ / / / / / /_/ /_/ / /  / / / / / /_/ /   ___/ /  __/ /   | |/ / / /__/  __/
                /_/  /_/\\____/_/ /_/_/\\__/\\____/_/  /_/_/ /_/\\__, /   /____/\\___/_/    |___/_/\\___/\\___/\s
                                                            /____/                                      \s
                """);

        System.out.println(
                        "+----------------------------------------------------------+\n" +
                        "|                     AVAILABLE COMMANDS                   |\n" +
                        "+----------------------------------------------------------+\n" +
                        "| '/register' - Register a new account                     |\n" +
                        "| '/login'    - Log into an existing account               |\n" +
                        "| '/exit'     - Exit the program                           |\n" +
                        "|                                                          |\n" +
                        "| During registration or login:                            |\n" +
                        "| '/back'     - Cancel the current action                  |\n" +
                        "+----------------------------------------------------------+"
        );
    }


    private static void showUserMenu() {
        System.out.println(
                        "+----------------------------------------------------------+\n" +
                        "|                        USER MENU                         |\n" +
                        "+----------------------------------------------------------+\n" +
                        "| /submitReading - Submit meter reading                    |\n" +
                        "| /getCurrentReadings - Get current readings               |\n" +
                        "| /getMonthlyReadings - Get readings for a specific month  |\n" +
                        "| /logout - Log out                                        |\n" +
                        "+----------------------------------------------------------+"
        );
    }

    private static void showAdminMenu() {
        System.out.println(
                        "+----------------------------------------------------------+\n" +
                        "|                       ADMIN MENU                         |\n" +
                        "+----------------------------------------------------------+\n" +
                        "| /submitReading - Submit meter reading                    |\n" +
                        "| /getCurrentReadings - Get current readings               |\n" +
                        "| /getMonthlyReadings - Get readings for a specific month  |\n" +
                        "| /viewHistory - View submission history                   |\n" +
                        "| /userRightsControl - Control user rights                 |\n" +
                        "| /audit - Audit user actions                              |\n" +
                        "| /logout - Log out                                        |\n" +
                        "+----------------------------------------------------------+"
        );
    }
}
