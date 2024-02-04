package monitoring.service.dev.out;

import monitoring.service.dev.models.Person;

public class OutputManager {

    private static OutputManager instance;

    private OutputManager(){}

    public static OutputManager getInstance(){
        if(instance==null){
            instance = new OutputManager();
        }
        return instance;
    }

    public void showIn(){
        System.out.print("> ");
    }

    public void showUnknownCommand(){
        System.out.println("Unknown command.");
    }

    public void showExit(){
        System.out.println("Exiting program...");
    }

    public void showLogout(){
        System.out.println("Logging out...");
    }

    public void showMissingUsernameOrPassword(){
        System.out.println("Username or password missing.");
    }

    public void showSuccessfulRegistration(Person person){
        System.out.println("Successful registration!"+
                "\nYou are ~ " + person.getUsername() + " ~ and your role is " + person.getRole());
    }

    public void showSuccessfulAuthentication(Person person){
        System.out.println("Successful authentication!" +
                "\nYou are ~ " + person.getUsername() + " ~ and your role is " + person.getRole());
    }

    public void showMissingSomeSubmitKey(){
        System.out.println("Some of the parameters are missing. The correct input form is '/submit -s (HOT or COLD) " +
                "-i (double indication) -m (month) -y (year)'+" +
                "\nFor example: /submit -s hot -i 200.14 -m January -y 2024");
    }

    public void showCorrectSubmit(){
        System.out.println("The correct input form is '/submit -s (HOT or COLD) " +
                "-i (double indication) -m (month) -y (year)'+" +
                "\nFor example: /submit -s hot -i 200.14 -m January -y 2024");
    }

    public void showCorrectRights(){
        System.out.println("The correct input form is '/rights -u (user's nickname to change) " +
                "-a (action: downgrade or upgrade)'\nFor example: /rights -u trapper -a upgrade");
    }

    public void show(RuntimeException e){
        System.out.println(e.getMessage());
    }

    public void show(String msg){
        System.out.println(msg);
    }

    public void showThereAreReadings(){
        System.out.println("Current indications:");
    }

    public void showSuccess(){
        System.out.println("Success!");
    }

    public void showAvailableCommands() {
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
                     :: Monitoring-Service By trapper for Y_LAB ::                   (v2.0.5)
                """);

        System.out.println(
                """
                        +----------------------------------------------------------+
                        |                     AVAILABLE COMMANDS                   |
                        +----------------------------------------------------------+
                        |'/register -u username -p password'   - Register          |
                        |               a new account                              |
                        |'/login -u username -p password'      - Log into an       |
                        |               existing account                           |
                        |'/exit'     - Exit the program                            |
                        +----------------------------------------------------------+"""
        );
    }

    public void showUserMenu() {
        System.out.println(
                """
                        +----------------------------------------------------------+
                        |                        USER MENU                         |
                        +----------------------------------------------------------+
                        |'/submit -s scanner -i indication -m month -y year'       |
                        |                     - Submit meter reading               |
                        |'/get'               - Get current readings               |
                        |'/getMonthly -m month -y year'                            |
                        |                     - Get readings for a specific month  |
                        |'/history'           - View submission history            |
                        |'/logout'            - Log out                            |
                        +----------------------------------------------------------+"""
        );
    }

    public void showAdminMenu() {
        System.out.println(
                """
                        +----------------------------------------------------------+
                        |                       ADMIN MENU                         |
                        +----------------------------------------------------------+
                        |'/submit -s scanner -i indication -m month -y year'       |
                        |                     - Submit meter reading               |
                        |'/get'               - Get current readings               |
                        |'/getMonthly -m month -y year'                            |
                        |                     - Get readings for a specific month  |
                        |'/history'           - View submission history            |
                        |'/rights -u username -a action                            |
                        |                     - Control user rights                |
                        |'/audit'             - Audit user action                  |
                        |'/logout'            - Log out                            |
                        +----------------------------------------------------------+"""
        );
    }
}
