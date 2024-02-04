package monitoring.service.dev;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.controllers.AdminController;
import monitoring.service.dev.controllers.DoController;
import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.models.History;
import monitoring.service.dev.out.OutputManager;
import monitoring.service.dev.out.SimpleHistory;
import monitoring.service.dev.out.SimpleLogger;
import monitoring.service.dev.utils.ArgsParser;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import monitoring.service.dev.utils.exceptions.MeterReadingExistsException;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;

public class SessionCommandProcessor {

    private static SessionCommandProcessor instance;

    private SessionCommandProcessor(){}

    public static SessionCommandProcessor getInstance(){
        if(instance==null){
            instance = new SessionCommandProcessor();
        }
        return instance;
    }

    private static final OutputManager printer = OutputManager.getInstance();
    private static final DoController doController = DoController.getInstance();
    private static final AdminController adminController = AdminController.getInstance();
    private static final SimpleLogger logger = SimpleLogger.getInstance();
    private static final SimpleHistory history = SimpleHistory.getInstance();

    public void submit(CredentialsDTO credentials, String args){
        Map<String, String> argsMap = ArgsParser.parseArgs(args);

        if(argsMap.size()<4){
            printer.showMissingSomeSubmitKey();
            return;
        }

        try {
            String type = argsMap.get(AppConstants.ARG_SCANNER);
            double indication = Double.parseDouble(argsMap.get(AppConstants.ARG_INDICATION));
            String month = argsMap.get(AppConstants.ARG_MONTH);
            int year = Integer.parseInt(argsMap.get(AppConstants.ARG_YEAR));

            SensorDTO sensorDTO = SensorDTO.builder()
                    .type("HOT".equalsIgnoreCase(type) ? SensorType.HOT_WATER_METERS : SensorType.COLD_WATER_METERS)
                    .readings(new ArrayList<>())
                    .build();


            String date = month + " " + year;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            YearMonth yearMonth = YearMonth.parse(date, formatter);
            LocalDateTime time = yearMonth.atDay(1).atStartOfDay();

            MeterReadingDTO meterReadingDTO = MeterReadingDTO.builder()
                    .indication(indication)
                    .date(time)
                    .build();

            sensorDTO.getReadings().add(meterReadingDTO);
            credentials.getSensors().add(sensorDTO);

            doController.submitReading(credentials);

            history.push(credentials);
            logger.logEventSubmitSuccess(credentials);

            printer.showSuccess();

        }catch (NumberFormatException | DateTimeParseException e) {
            printer.showCorrectSubmit();
            logger.logEventUsernameAndError("SUBMIT",credentials,e.getMessage());
        }catch (NotValidException | MeterReadingExistsException | NotFoundException e) {
            printer.show(e);
            logger.logEventUsernameAndError("SUBMIT",credentials,e.getMessage());
        }
    }

    public void get(CredentialsDTO credentials){
        List<SensorDTO> sensors = doController.getCurrentReadings(credentials);
        if (sensors.isEmpty()) {
            String message = "There are no current indicators";
            printer.show(message);
            logger.logEventUsernameAndError("GET",credentials,message);

        } else {
            printer.showThereAreReadings();
            for (SensorDTO sensor : sensors) {
                printer.show(" - Sensor: " + sensor.getType() + " - indication - " + sensor.getReadings().get(0).getIndication());
            }

            logger.LogEventUsername("GET",credentials);
        }
    }

    public void getMonthly(CredentialsDTO credentials, String args){
        Map<String, String> argsMap = ArgsParser.parseArgs(args);

        String parsMonth = argsMap.get(AppConstants.ARG_MONTH);
        String parsYear = argsMap.get(AppConstants.ARG_YEAR);

        if (parsMonth == null || parsYear == null) {
            printer.show("Missing month or year in the arguments.");
            return;
        }

        Month month;
        try {
            month = Month.valueOf(parsMonth.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            printer.show("Invalid month: " + parsMonth + "\nExpected format: " + getExpectedMonthFormat());
            return;
        }

        try {
            Integer.parseInt(parsYear);
        } catch (NumberFormatException e) {
            printer.show("Invalid year: " + parsYear);
            return;
        }

        List<SensorDTO> monthlyReadings;
        try {
            monthlyReadings = doController.getMonthlyReadings(credentials, parsMonth, parsYear);
        }catch (NotValidException e){
            printer.show(e.getMessage());
            return;
        }

        if (monthlyReadings.isEmpty()) {
            String message = "No readings found for " + month + " " + parsYear;
            printer.show(message);
            logger.logEventUsernameAndError("GET MONTHLY", credentials, message);

        } else {
            printer.show("Readings for " + month + " " + parsYear + ":");
            for (SensorDTO sensor : monthlyReadings) {
                printer.show("Sensor Type: " + sensor.getType());
                for (MeterReadingDTO reading : sensor.getReadings()) {
                    printer.show(" - Indication: " + reading.getIndication() + ", Date: " + reading.getDate().toLocalDate());
                }
            }

            logger.LogEventUsername("GET MONTHLY",credentials);
        }
    }

    private String getExpectedMonthFormat() {
        StringBuilder month = new StringBuilder();
        for (Month m : Month.values()) {
            if (!month.isEmpty()) month.append(", ");
            month.append(m.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        return month.toString();
    }

    public void history(CredentialsDTO credentials){
        List<History> histories = doController.getHistory(credentials);
        if(histories.isEmpty()){
            String message = "There are no actions";
            printer.show(message);
            logger.logEventUsernameAndError("HISTORY",credentials,message);

        }else {
            printer.show("The history of meter readings submitting for "+credentials.getUsername());
            for(History history : histories){
                printer.show(" - ACTION: "+history.getAction()+" AT TIME "+history.getTime());
            }

            logger.LogEventUsername("HISTORY",credentials);
        }
    }

    public void rights(Role role, CredentialsDTO credentials, String args){
        if (role == Role.ADMIN) {
            Map<String, String> argsMap = ArgsParser.parseArgs(args);

            String username = argsMap.get(AppConstants.ARG_USERNAME);
            String action = argsMap.get(AppConstants.ARG_ACTION);

            if (username == null || action == null) {
                printer.show("Missing username or action in the arguments.");
                printer.showCorrectRights();
                return;
            }

            if(username.equals(credentials.getUsername())){
                printer.show("You cannot change your rights");
                return;
            }

            try {
                switch (action){
                    case AppConstants.UPGRADE -> {
                        adminController.setAuthorities(username);
                        logger.logEventRightsToAdminSuccess(credentials,username);
                    }
                    case AppConstants.DOWNGRADE -> {
                        adminController.deleteAuthorities(username);
                        logger.logEventRightsToUserSuccess(credentials,username);
                    }
                    default -> {
                        printer.show("incorrect action");
                        return;
                    }
                }

            }catch (NotFoundException | CanNotDoException e){
                printer.show(e.getMessage());
                logger.logEventUsernameRoleAndError("RIGHTS",credentials,e.getMessage(),role);
            }

            printer.show("Users and their rights after changes: ");
            List<CredentialsDTO> credentialsDTOS = adminController.getAllUsers();
            for(CredentialsDTO user : credentialsDTOS){
                printer.show(" - USERNAME: "+ (user.getUsername().equals(credentials.getUsername()) ?
                        user.getUsername() + " RIGHTS: "+user.getRole() +" - ITS YOU" :
                        user.getUsername() + " RIGHTS: "+user.getRole()));
            }

        } else {
            String message = "403 FORBIDDEN (Unauthorized action)";
            printer.show(message);
            logger.logEventUsernameRoleAndError("RIGHTS",credentials,message,role);
        }
    }

    public void audit(Role role, CredentialsDTO credentials){
        if (role == Role.ADMIN) {

            List<Audit> audits = adminController.getAudit();

            if(audits.isEmpty()){
                String message = "There are no audits";
                printer.show(message);
                logger.logEventUsernameRoleAndError("AUDIT",credentials,message,role);
            }else{
                for(Audit audit :audits){
                    printer.show(audit.getLog());
                }

                logger.logEventAuditSuccess(credentials);

            }
        } else {
            String message = "403 FORBIDDEN (Unauthorized action)";
            printer.show(message);
            logger.logEventUsernameRoleAndError("AUDIT",credentials,message,role);
        }
    }

    public boolean logout(CredentialsDTO credentials){
        boolean isSessionActive = false;

        printer.showLogout();
        logger.LogEventUsername("LOGOUT",credentials);

        return isSessionActive;
    }
}
