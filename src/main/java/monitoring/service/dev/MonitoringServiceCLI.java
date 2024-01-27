package monitoring.service.dev;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.controllers.AdminController;
import monitoring.service.dev.controllers.AuthController;
import monitoring.service.dev.controllers.DoController;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.models.History;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import monitoring.service.dev.utils.exceptions.MeterReadingExistsException;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.mappers.PersonMapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MonitoringServiceCLI {
    private static final Scanner keyboard = new Scanner(System.in);
    private static final PersonMapper pMapper = Mappers.getMapper(PersonMapper.class);
    private static final AuthController auth = AuthController.getInstance();
    private static final DoController doController = DoController.getInstance();
    private static final AdminController adminController = AdminController.getInstance();

    public static void main(String[] args) {
        showAvailableCommands();

        String action;
        while (true) {
            System.out.print("> ");
            action = keyboard.nextLine();

            switch (action.toLowerCase()) {
                case "/register" ->{
                    Person person = handleRegistration();


                    adminController.postAudit(Audit.builder()
                            .log("["+LocalDateTime.now()+"] REGISTRATION "+ (person!=null ?
                                    "SUCCESS\n - username: " + person.getUsername() : "ERROR"))
                            .build());

                    handleSession(person);
                }
                case "/login" ->{
                    Person person = handleLogin();

                    adminController.postAudit(Audit.builder()
                            .log("["+LocalDateTime.now()+"] LOGIN "+ (person!=null ?
                                    "SUCCESS\n - username: " + person.getUsername() : "ERROR"))
                            .build());

                    handleSession(person);
                }
                case "/exit" ->{
                    System.out.println("Exiting program...");
                    return;
                }
                default -> System.out.println("Unknown command.");
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
        if(person==null) return;

        Role role = person.getRole();

        if (role == Role.USER) {
            showUserMenu();
        } else if (role == Role.ADMIN) {
            showAdminMenu();
        }

        CredentialsDTO credentials = pMapper.convertToCredentialsDTO(person);

        boolean isSessionActive = true;
        while (isSessionActive) {

            System.out.print("> ");
            String command = keyboard.nextLine();

            credentials.setSensors(new ArrayList<>());

            switch (command) {
                case "/submit" -> {
                    try {
                        System.out.println("for submitting you need tap type sensor 1 - HOT 2 - COLD");
                        int type = keyboard.nextInt();
                        keyboard.nextLine();

                        SensorDTO sensorDTO = SensorDTO.builder()
                                .type(type == 1 ? SensorType.HOT_WATER_METERS : SensorType.COLD_WATER_METERS)
                                .readings(new ArrayList<>())
                                .build();

                        System.out.println("enter indication");
                        double indication = keyboard.nextDouble();
                        keyboard.nextLine();

                        YearMonth yearMonth;
                        while(true){
                            System.out.println("enter month and year (e.g. March 2021)");
                            String dateString = keyboard.nextLine();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
                            try {
                                yearMonth = YearMonth.parse(dateString, formatter);
                                break;
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter in 'MMMM yyyy' format (e.g. March 2021).");
                            }
                        }

                        LocalDateTime dateTime = yearMonth.atDay(1).atStartOfDay(); // Получаем начало первого дня указанного месяца
                        MeterReadingDTO meterReadingDTO = MeterReadingDTO.builder()
                                .indication(indication)
                                .date(dateTime)
                                .build();

                        sensorDTO.getReadings().add(meterReadingDTO);
                        credentials.getSensors().add(sensorDTO);

                        doController.submitReading(credentials);

                        doController.pushHistory(
                                History.builder()
                                        .credentials(credentials)
                                        .action("SUBMIT ("+credentials.getSensors().get(0).getType()+") WITH READINGS: "
                                                +credentials.getSensors().get(0).getReadings().get(0).getIndication())
                                        .time(LocalDateTime.now())
                                        .build());

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] SUBMIT SUCCESS" +
                                        "\n - username: "+credentials.getUsername()+
                                        "\n - sensor: "+credentials.getSensors().get(0).getType()+
                                        "\n - readings: "+credentials.getSensors().get(0).getReadings().get(0).getIndication())
                                .build());

                        System.out.println("Success!");

                    } catch (NotValidException | MeterReadingExistsException | NotFoundException e) {
                        System.out.println(e.getMessage());

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] SUBMIT ERROR" +
                                        "\n - username: "+credentials.getUsername()+
                                        "\n - error: "+e.getMessage())
                                .build());
                    }
                }
                case "/get" -> {
                    List<SensorDTO> sensors = doController.getCurrentReadings(credentials);
                    if (sensors.isEmpty()) {
                        String message = "There are no current indicators";
                        System.out.println(message);

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] GET ERROR" +
                                        "\n - username: "+credentials.getUsername()+
                                        "\n - error: "+message)
                                .build());

                    } else {
                        for (SensorDTO sensor : sensors) {
                            System.out.println(sensor.getType() + " " + sensor.getReadings().get(0).getIndication()
                                    + " " + sensor.getReadings().get(0).getDate());
                        }

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] GET SUCCESS" +
                                        "\n - username: "+credentials.getUsername())
                                .build());
                    }
                }
                case "/getMonthly" -> {

                    System.out.println("Enter the month (e.g., January):");
                    String month = keyboard.nextLine().trim();
                    System.out.println("Enter the year (e.g., 2024):");
                    String year = keyboard.nextLine().trim();

                    // Получение показаний
                    List<SensorDTO> monthlyReadings = doController.getMonthlyReadings(credentials, month, year);

                    // Проверка и вывод результатов
                    if (monthlyReadings.isEmpty()) {
                        String message = "No readings found for " + month + " " + year;
                        System.out.println(message);

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] GET MONTHLY ERROR" +
                                        "\n - username: "+credentials.getUsername()+
                                        "\n - error: "+message)
                                .build());

                    } else {
                        System.out.println("Readings for " + month + " " + year + ":");
                        for (SensorDTO sensor : monthlyReadings) {
                            System.out.println("Sensor Type: " + sensor.getType());
                            for (MeterReadingDTO reading : sensor.getReadings()) {
                                System.out.println(" - Indication: " + reading.getIndication() + ", Date: " + reading.getDate().toLocalDate());
                            }
                        }

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] GET MONTHLY SUCCESS" +
                                        "\n - username: "+credentials.getUsername())
                                .build());
                    }
                }
                case "/history" -> {
                    List<History> histories = doController.getHistory(credentials);
                    if(histories.isEmpty()){
                        String message = "There are no actions";
                        System.out.println(message);

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] HISTORY ERROR" +
                                        "\n - username: "+credentials.getUsername()+
                                        "\n - error: "+message)
                                .build());

                    }else {
                        System.out.println("The history of meter readings submitting for "+credentials.getUsername());
                        for(History history : histories){
                            System.out.println(" - ACTION: "+history.getAction()+" AT TIME "+history.getTime());
                        }

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] HISTORY SUCCESS" +
                                        "\n - username: "+credentials.getUsername())
                                .build());
                    }
                }
                case "/rights" -> {
                    if (role == Role.ADMIN) {

                        System.out.println("USERS AND THEM RIGHTS");
                        List<CredentialsDTO> credentialsDTOS = adminController.getAllUsers();
                        for(CredentialsDTO user : credentialsDTOS){
                            System.out.println(" - USERNAME: "+ (user.getUsername().equals(credentials.getUsername()) ?
                                    user.getUsername() + " RIGHTS: "+user.getRole() +" - ITS YOU" :
                                    user.getUsername() + " RIGHTS: "+user.getRole()));
                        }

                        String username;
                        while (true){
                            System.out.println("Write the username of the person whose rights you want to change");
                            username = keyboard.nextLine();
                            if(username.equals(credentials.getUsername())){
                                System.out.println("You cannot change your rights");
                            }else break;
                        }

                        int action;
                        do {
                            System.out.println("Write 1 to raise and 2 to lower");
                            action = keyboard.nextInt();
                            keyboard.nextLine();
                        } while (action != 1 && action != 2);

                        try {
                            switch (action){
                                case 1 -> {
                                    adminController.setAuthorities(username);

                                    adminController.postAudit(Audit.builder()
                                            .log("["+LocalDateTime.now()+"] RIGHTS SUCCESS" +
                                                    "\n - username: "+credentials.getUsername()+
                                                    "\n - role: "+credentials.getRole()+
                                                    "\n - action: "+username+" -> ADMIN")
                                            .build());
                                }
                                case 2 -> {
                                    adminController.deleteAuthorities(username);

                                    adminController.postAudit(Audit.builder()
                                            .log("["+LocalDateTime.now()+"] RIGHTS SUCCESS" +
                                                    "\n - username: "+credentials.getUsername()+
                                                    "\n - role: "+credentials.getRole()+
                                                    "\n - action: "+username+" -> USER")
                                            .build());
                                }
                            }
                            System.out.println("Success!");
                        }catch (NotFoundException | CanNotDoException e){
                            System.out.println(e.getMessage());

                            adminController.postAudit(Audit.builder()
                                    .log("["+LocalDateTime.now()+"] RIGHTS ERROR" +
                                            "\n - username: "+credentials.getUsername()+
                                            "\n - role: "+credentials.getRole()+
                                            "\n - error: "+e.getMessage())
                                    .build());
                        }

                    } else {
                        String message = "403 FORBIDDEN (Unauthorized action)";
                        System.out.println(message);

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] RIGHTS ERROR" +
                                        "\n - username: "+credentials.getUsername()+
                                        "\n - role: "+credentials.getRole()+
                                        "\n - error: "+message)
                                .build());
                    }
                }
                case "/audit" -> {
                    if (role == Role.ADMIN) {

                        List<Audit> audits = adminController.getAudit();

                        if(audits.isEmpty()){
                            String message = "There are no audits";
                            System.out.println(message);

                            adminController.postAudit(Audit.builder()
                                    .log("["+LocalDateTime.now()+"] AUDIT ERROR" +
                                            "\n - username: "+credentials.getUsername()+
                                            "\n - role: "+credentials.getRole()+
                                            "\n - error: "+message)
                                    .build());
                        }else{
                            for(Audit audit :audits){
                                System.out.println(audit.getLog());
                            }

                            adminController.postAudit(Audit.builder()
                                    .log("["+LocalDateTime.now()+"] AUDIT SUCCESS" +
                                            "\n - username: "+credentials.getUsername()+
                                            "\n - role: "+credentials.getRole())
                                    .build());

                        }
                    } else {
                        String message = "403 FORBIDDEN (Unauthorized action)";
                        System.out.println(message);

                        adminController.postAudit(Audit.builder()
                                .log("["+LocalDateTime.now()+"] AUDIT ERROR" +
                                        "\n - username: "+credentials.getUsername()+
                                        "\n - role: "+credentials.getRole()+
                                        "\n - error: "+message)
                                .build());
                    }
                }
                case "/logout" -> {
                    isSessionActive = false;

                    adminController.postAudit(Audit.builder()
                            .log("["+LocalDateTime.now()+"] LOGOUT" +
                                    "\n - username: "+credentials.getUsername())
                            .build());

                    System.out.println("Logging out...");
                }
                default -> System.out.println("Unknown command.");
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
                     :: Monitoring-Service By trapper for Y_LAB ::                   (v1.0.7)                          
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
                        "| /submit             - Submit meter reading               |\n" +
                        "| /get                - Get current readings               |\n" +
                        "| /getMonthly         - Get readings for a specific month  |\n" +
                        "| /history            - View submission history            |\n" +
                        "| /logout             - Log out                            |\n" +
                        "+----------------------------------------------------------+"
        );
    }

    private static void showAdminMenu() {
        System.out.println(
                        "+----------------------------------------------------------+\n" +
                        "|                       ADMIN MENU                         |\n" +
                        "+----------------------------------------------------------+\n" +
                        "| /submit             - Submit meter reading               |\n" +
                        "| /get                - Get current readings               |\n" +
                        "| /getMonthly         - Get readings for a specific month  |\n" +
                        "| /history            - View submission history            |\n" +
                        "| /rights             - Control user rights                |\n" +
                        "| /audit              - Audit user action                  |\n" +
                        "| /logout             - Log out                            |\n" +
                        "+----------------------------------------------------------+"
        );
    }
}