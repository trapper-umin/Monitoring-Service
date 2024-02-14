package monitoring.service.dev.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.controllers.interfaces.IDoController;
import monitoring.service.dev.dtos.ReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.responses.HistoryDTOResp;
import monitoring.service.dev.dtos.responses.CommonResp;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.requests.CredentialsDTOWithSensorReqst;
import monitoring.service.dev.dtos.requests.SensorDTOWithOneReadingReqst;
import monitoring.service.dev.dtos.requests.SensorReadingReqst;
import monitoring.service.dev.dtos.responses.PersonWithSensorsAndReadingsDTOResp;
import monitoring.service.dev.models.History;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.out.Sandler;
import monitoring.service.dev.repositories.jdbc.HistoryRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.repositories.jdbc.ReadingsRepository;
import monitoring.service.dev.services.DoService;
import monitoring.service.dev.services.HistoryService;
import monitoring.service.dev.services.JWTService;
import monitoring.service.dev.utils.annotations.DoAudit;
import monitoring.service.dev.utils.exceptions.JWTException;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import monitoring.service.dev.utils.mappers.HistoryMapper;
import monitoring.service.dev.utils.mappers.PersonMapper;
import org.mapstruct.factory.Mappers;

@WebServlet("/do/*")
public class ImplDoController extends HttpServlet implements IDoController {

    private final PeopleRepository peopleRepository = new PeopleRepository();
    private final ReadingsRepository readingsRepository = new ReadingsRepository();
    private final HistoryRepository historyRepository = new HistoryRepository();
    private final JWTService jwtService = new JWTService(peopleRepository);
    private final DoService doService = new DoService(peopleRepository, readingsRepository);
    private final HistoryService historyService = new HistoryService(historyRepository);
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);
    private final HistoryMapper historyMapper = Mappers.getMapper(HistoryMapper.class);
    private final ObjectMapper jackson = new ObjectMapper();
    private final Sandler sandler = new Sandler(jackson);

    public ImplDoController() {
        jackson.registerModule(new JavaTimeModule());
    }

    @DoAudit
    @Override
    public List<SensorDTO> getCurrentReadings(CredentialsDTOReqst credentials) {
        return doService.getCurrentReadings(credentials);
    }

    @DoAudit
    @Override
    public List<SensorDTO> getMonthlyReadings(CredentialsDTOReqst credentials, String month,
        String year) {
        return doService.getMonthlyReadings(credentials, month, year);
    }

    @DoAudit
    @Override
    public List<History> getHistory(CredentialsDTOReqst credentials) {
        return historyService.get(credentials);
    }

    @Override
    public void pushHistory(History history) {
        historyService.push(history);
    }

    @DoAudit
    @Override
    public void submitReading(CredentialsDTOWithSensorReqst credentials) {
        doService.submitReading(credentials);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String token = req.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Authorization token is required");
            }

            token = token.substring(7);
            Person person = validateToken(resp, token);
            if (person == null) {
                throw new IllegalArgumentException("Invalid or expired token");
            }

            SensorReadingReqst readingRequest = jackson.readValue(req.getInputStream(),
                SensorReadingReqst.class);
            CredentialsDTOWithSensorReqst credentials = mapPersonToCredentialsWithSensor(person,
                readingRequest);
            submitReading(credentials);
            pushHistory(History.builder().action(
                    "SUBMIT (" + credentials.getSensor().getType() + ") WITH READINGS: "
                        + credentials.getSensor().getReading().getIndication() + " BY "
                        + credentials.getSensor().getReading().getDate()).time(LocalDateTime.now())
                .username(person.getUsername()).build());

            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException | IOException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotValidException | ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        } catch (Exception e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String token = req.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                "Authorization token is required");
            return;
        }

        token = token.substring(7);
        Person person = validateToken(resp, token);
        if (person == null) {
            return;
        }

        String path = req.getPathInfo();
        CredentialsDTOReqst credentials = personMapper.convertToCredentialsDTOReqst(person);

        switch (path) {
            case "/current" -> processCurrentReadings(req, resp, credentials, person);
            case "/monthly" -> processMonthlyReadings(req, resp, credentials, person);
            case "/history" -> processHistory(resp, credentials);
            default -> sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                "Unknown request path");
        }
    }

    private void processCurrentReadings(HttpServletRequest req, HttpServletResponse resp,
        CredentialsDTOReqst credentials, Person person) {
        try {
            List<SensorDTO> currentReadings = getCurrentReadings(credentials);
            sandler.sendSuccessResponse(resp,
                PersonWithSensorsAndReadingsDTOResp.builder().status(HttpServletResponse.SC_OK)
                    .operation("get current readings").time(LocalDateTime.now())
                    .user(person.getUsername()).sensors(currentReadings).build());
        } catch (NotFoundException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        }
    }

    private void processMonthlyReadings(HttpServletRequest req, HttpServletResponse resp,
        CredentialsDTOReqst credentials, Person person) {
        try {
            String month = req.getParameter("month");
            String year = req.getParameter("year");
            List<SensorDTO> monthlyReadings = getMonthlyReadings(credentials, month, year);
            sandler.sendSuccessResponse(resp,
                PersonWithSensorsAndReadingsDTOResp.builder().status(HttpServletResponse.SC_OK)
                    .operation("get readings for " + month + " " + year).time(LocalDateTime.now())
                    .user(person.getUsername()).sensors(monthlyReadings).build());
        } catch (NotFoundException | NotValidException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        }
    }

    private void processHistory(HttpServletResponse resp, CredentialsDTOReqst credentials) {
        try {
            List<History> histories = getHistory(credentials);
            List<HistoryDTOResp> historiesDTO = historyMapper.convertToHistoryDTOList(histories);
            sandler.sendSuccessResponse(resp,
                new CommonResp<>(HttpServletResponse.SC_OK,
                    "histories of submitting for " + credentials.getUsername(), LocalDateTime.now(),
                    historiesDTO));
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        }
    }

    private Person validateToken(HttpServletResponse resp, String token) {
        try {
            return jwtService.validate(token);
        } catch (NotFoundException | ProblemWithSQLException | JWTException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return null;
        }
    }

    private CredentialsDTOWithSensorReqst mapPersonToCredentialsWithSensor(Person person,
        SensorReadingReqst request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
        String monthYear = request.getMonth() + "-" + request.getYear();
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(monthYear, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "The month or year does not match the required pattern (e.g., 'January', '2024')");
        }
        LocalDateTime date = yearMonth.atDay(1).atStartOfDay();

        ReadingDTO reading = ReadingDTO.builder().indication(request.getIndication()).date(date)
            .build();

        SensorDTOWithOneReadingReqst sensor = SensorDTOWithOneReadingReqst.builder().type(
            request.getSensor().equals("HOT") ? SensorType.HOT_WATER_METERS
                : SensorType.COLD_WATER_METERS).reading(reading).build();

        return CredentialsDTOWithSensorReqst.builder().username(person.getUsername()).sensor(sensor)
            .build();
    }
}
