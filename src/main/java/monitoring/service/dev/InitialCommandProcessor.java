package monitoring.service.dev;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.controllers.AuthController;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.out.OutputManager;
import monitoring.service.dev.utils.ArgsParser;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import monitoring.service.dev.utils.mappers.PersonMapper;
import org.mapstruct.factory.Mappers;

public class InitialCommandProcessor {

    private static final Scanner keyboard = new Scanner(System.in);
    private static final PersonMapper pMapper = Mappers.getMapper(PersonMapper.class);
    private static final OutputManager printer = OutputManager.getInstance();
    private static final SessionCommandProcessor handler = SessionCommandProcessor.getInstance();
    private static final AuthController auth = AuthController.getInstance();
    private static InitialCommandProcessor instance;

    private InitialCommandProcessor() {
    }

    public static InitialCommandProcessor getInstance() {
        if (instance == null) {
            instance = new InitialCommandProcessor();
        }
        return instance;
    }

    public Person handleRegistration(String args) {
        Map<String, String> argsMap = ArgsParser.parseArgs(args);
        String username = argsMap.get(AppConstants.ARG_USERNAME);
        String password = argsMap.get(AppConstants.ARG_PASSWORD);

        if (username == null || password == null) {
            printer.showMissingUsernameOrPassword();
            return null;
        }

        try {
            CredentialsDTO dto = CredentialsDTO.builder().username(username).password(password)
                .build();

            Person person = auth.registration(dto);
            printer.showSuccessfulRegistration(person);

            return person;
        } catch (NotValidException | ProblemWithSQLException e) {
            printer.show(e);
            return null;
        }
    }

    public Person handleLogin(String args) {
        Map<String, String> argsMap = ArgsParser.parseArgs(args);
        String username = argsMap.get(AppConstants.ARG_USERNAME);
        String password = argsMap.get(AppConstants.ARG_PASSWORD);

        if (username == null || password == null) {
            printer.showMissingUsernameOrPassword();
            return null;
        }

        try {
            CredentialsDTO dto = CredentialsDTO.builder().username(username).password(password)
                .build();

            Person person = auth.authentication(dto);
            printer.showSuccessfulAuthentication(person);

            return person;
        } catch (NotFoundException | NotValidException | ProblemWithSQLException e) {
            printer.show(e);
            return null;
        }
    }

    public void handleSession(Person person) {
        if (person == null) {
            return;
        }

        Role role = person.getRole();

        if (role == Role.USER) {
            printer.showUserMenu();
        } else if (role == Role.ADMIN) {
            printer.showAdminMenu();
        }

        CredentialsDTO credentials = pMapper.convertToCredentialsDTO(person);

        boolean isSessionActive = true;
        while (isSessionActive) {

            printer.showIn();
            String input = keyboard.nextLine().trim();
            String[] parts = input.split("\\s+", 2);
            String command = parts[0];
            String args = parts.length > 1 ? parts[1] : "";

            credentials.setSensors(new ArrayList<>());

            switch (command) {
                case AppConstants.COMMAND_SUBMIT -> handler.submit(credentials, args);
                case AppConstants.COMMAND_GET -> handler.get(credentials);
                case AppConstants.COMMAND_GET_MONTHLY -> handler.getMonthly(credentials, args);
                case AppConstants.COMMAND_HISTORY -> handler.history(credentials);
                case AppConstants.COMMAND_RIGHTS -> handler.rights(role, credentials, args);
                case AppConstants.COMMAND_AUDIT -> handler.audit(role, credentials);
                case AppConstants.COMMAND_LOGOUT -> isSessionActive = handler.logout(credentials);
                default -> printer.showUnknownCommand();
            }
        }
    }
}
