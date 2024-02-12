package monitoring.service.dev.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.controllers.interfaces.IDoController;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.requests.CredentialsDTOWithSensorReqst;
import monitoring.service.dev.dtos.requests.ErrorMessage;
import monitoring.service.dev.dtos.responses.PersonWithSensorsAndReadingsDTOResp;
import monitoring.service.dev.models.History;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.jdbc.HistoryRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.repositories.jdbc.ReadingsRepository;
import monitoring.service.dev.services.DoService;
import monitoring.service.dev.services.HistoryService;

import java.util.List;
import monitoring.service.dev.services.JWTService;
import monitoring.service.dev.utils.exceptions.JWTException;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
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
    private final ObjectMapper jackson = new ObjectMapper();

    public ImplDoController(){
        jackson.registerModule(new JavaTimeModule());
    }

    @Override
    public List<SensorDTO> getCurrentReadings(CredentialsDTOReqst credentials) {
        return doService.getCurrentReadings(credentials);
    }

    @Override
    public List<SensorDTO> getMonthlyReadings(CredentialsDTOReqst credentials,
        String month, String year) {
        return doService.getMonthlyReadings(credentials, month, year);
    }

    @Override
    public List<History> getHistory(CredentialsDTOReqst credentials) {
        return historyService.get(credentials);
    }

    @Override
    public void pushHistory(History history) {
        historyService.push(history);
    }

    @Override
    public void submitReading(CredentialsDTOWithSensorReqst credentials) {
        doService.submitReading(credentials);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String token = req.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Person person = null;
            try {
                person = jwtService.validate(token);
            }catch (NotFoundException | JWTException e){
                resp.setContentType("application/json");
                jackson.writeValue(resp.getOutputStream(), ErrorMessage.builder()
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .time(LocalDateTime.now())
                    .error(e.getMessage())
                    .build());
            }
            if (person!=null) {
                String path = req.getPathInfo();
                CredentialsDTOReqst credentials = null;
                //CredentialsDTOReqst credentials = personMapper.convertToCredentialsDTOReqst(person);

                try {
                    switch (path) {
                        case "/current" ->{
                            try {
                                credentials = personMapper.convertToCredentialsDTOReqst(person);
                                List<SensorDTO> currentReadings = getCurrentReadings(credentials);
                                resp.setContentType("application/json");
                                jackson.writeValue(resp.getOutputStream(),
                                    PersonWithSensorsAndReadingsDTOResp.builder()
                                        .status(HttpServletResponse.SC_OK)
                                        .operation("get current")
                                        .time(LocalDateTime.now())
                                        .user(person.getUsername())
                                        .sensors(currentReadings)
                                        .build());
                            }catch (NotFoundException e){
                                resp.setContentType("application/json");
                                jackson.writeValue(resp.getOutputStream(), ErrorMessage.builder()
                                    .status(HttpServletResponse.SC_BAD_REQUEST)
                                    .time(LocalDateTime.now())
                                    .error(e.getMessage())
                                    .build());
                            }catch (ProblemWithSQLException e) {
                                resp.setContentType("application/json");
                                jackson.writeValue(resp.getOutputStream(), ErrorMessage.builder()
                                    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                    .time(LocalDateTime.now())
                                    .error(e.getMessage())
                                    .build());
                            }
                        }
                        case "/monthly" ->{
                            String month = req.getParameter("month");
                            String year = req.getParameter("year");
                            List<SensorDTO> monthlyReadings = getMonthlyReadings(credentials, month, year);
                            // Отправить monthlyReadings в ответе
                        }
                        case "/history"->{
                            List<History> history = getHistory(credentials);
                            // Отправить history в ответе
                        }
                        default ->{
                            resp.setContentType("application/json");
                            jackson.writeValue(resp.getOutputStream(), ErrorMessage.builder()
                                .status(HttpServletResponse.SC_BAD_REQUEST)
                                .time(LocalDateTime.now())
                                .error("Unknown request path")
                                .build());
                        }
                    }
                } catch (Exception e) {
                    resp.setContentType("application/json");
                    jackson.writeValue(resp.getOutputStream(), ErrorMessage.builder()
                        .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .time(LocalDateTime.now())
                        .error("Server error occurred")
                        .build());
                }
            }
        } else {
            resp.setContentType("application/json");
            jackson.writeValue(resp.getOutputStream(), ErrorMessage.builder()
                .status(HttpServletResponse.SC_BAD_REQUEST)
                .time(LocalDateTime.now())
                .error("Authorization token is required")
                .build());
        }
    }
}
