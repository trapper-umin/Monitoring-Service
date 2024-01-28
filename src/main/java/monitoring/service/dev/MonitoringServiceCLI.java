package monitoring.service.dev;

import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.out.OutputManager;
import monitoring.service.dev.out.SimpleLogger;

import java.util.Scanner;

public class MonitoringServiceCLI {
    private static final Scanner keyboard = new Scanner(System.in);
    private static final InitialCommandProcessor handler = InitialCommandProcessor.getInstance();
    private static final OutputManager printer = OutputManager.getInstance();
    private static final SimpleLogger logger = SimpleLogger.getInstance();


    public static void main(String[] args) {
        printer.showAvailableCommands();

        run();

    }

    private static void run(){

        String inputLine;
        boolean isActive=true;

        while (isActive) {
            printer.showIn();

            inputLine = keyboard.nextLine().trim();
            String[] tokens = inputLine.split("\\s+");

            if (tokens.length == 0) continue;

            String command = tokens[0].toLowerCase();
            String commandArgs = inputLine.substring(command.length()).trim();

            isActive=commandHandler(command,commandArgs);
        }
    }

    private static boolean commandHandler(String command, String args){
        switch (command.toLowerCase()) {
            case AppConstants.COMMAND_REGISTER ->{
                Person person = handler.handleRegistration(args);

                logger.logEventRegLogIn("REGISTRATION", person);

                handler.handleSession(person);
            }
            case AppConstants.COMMAND_LOGIN ->{
                Person person = handler.handleLogin(args);

                logger.logEventRegLogIn("LOGIN", person);

                handler.handleSession(person);
            }
            case AppConstants.COMMAND_EXIT ->{
                printer.showExit();
                return false;
            }
            default -> printer.showUnknownCommand();
        }
        return true;
    }
}