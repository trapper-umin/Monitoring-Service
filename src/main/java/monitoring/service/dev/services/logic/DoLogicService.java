package monitoring.service.dev.services.logic;

import static monitoring.service.dev.utils.Handler.handleErrors;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.dtos.ReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.requests.CredentialsDTOWithSensorReqst;
import monitoring.service.dev.dtos.requests.SensorDTOWithOneReadingReqst;
import monitoring.service.dev.dtos.requests.SubmitDTOReqst;
import monitoring.service.dev.dtos.responses.HistoryDTOResp;
import monitoring.service.dev.dtos.responses.PersonWithSensorsAndReadingsDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.models.History;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.services.db.DoService;
import monitoring.service.dev.services.db.HistoryService;
import monitoring.service.dev.services.db.JWTService;
import monitoring.service.dev.utils.mappers.HistoryMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class DoLogicService {

    private final JWTService jwtService;
    private final DoService doService;
    private final HistoryService historyService;
    private final HistoryMapper historyMapper;

    public DoLogicService(JWTService jwtService, DoService doService, HistoryService historyService,
        HistoryMapper historyMapper) {

        this.jwtService = jwtService;
        this.doService = doService;
        this.historyService = historyService;
        this.historyMapper = historyMapper;
    }

    public ResponseEntity<PersonWithSensorsAndReadingsDTOResp> getCurrentReadings(String token) {
        Person person = jwtProcessor(token);

        List<SensorDTO> sensors = doService.getCurrentReadings(CredentialsDTOReqst.builder()
            .username(person.getUsername()).build());

        return constructResponseEntity(person.getUsername(), sensors, "GET CURRENT (OK)");
    }

    public ResponseEntity<PersonWithSensorsAndReadingsDTOResp> getMonthlyReadings(String token,
        String month, String year) {
        Person person = jwtProcessor(token);

        List<SensorDTO> sensors = doService.getMonthlyReadings(CredentialsDTOReqst.builder()
            .username(person.getUsername()).build(),
            month,year);

        return constructResponseEntity(person.getUsername(), sensors,"GET MONTHLY (OK)");
    }

    public ResponseEntity<WrapperResp<HistoryDTOResp>> getHistory(String token) {
        Person person = jwtProcessor(token);
        List<HistoryDTOResp> histories = historyMapper.convertToHistoryDTOList(
            historyService.get(person.getUsername()));
        return new ResponseEntity<>(new WrapperResp<>(
                HttpServletResponse.SC_OK,
                "OK",
                LocalDateTime.now(),
                histories
            ),
            HttpStatus.OK
        );
    }

    public ResponseEntity<HttpStatus> submitReading(String token, SubmitDTOReqst submit,
        BindingResult bindingResult) {
        handleErrors(bindingResult);
        Person person = jwtProcessor(token);

        LocalDateTime date = constructDate(submit.getMonth(), submit.getYear());
        doService.submitReading(constructSubmitData(person,submit,date));
        processPushHistory(person, submit, date);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Person jwtProcessor(String token) {
        token = jwtService.extractToken(token);
        return jwtService.validate(token);
    }

    private ResponseEntity<PersonWithSensorsAndReadingsDTOResp> constructResponseEntity(String username,
        List<SensorDTO> sensors, String operation){
        return new ResponseEntity<>(
            new PersonWithSensorsAndReadingsDTOResp(
                HttpServletResponse.SC_OK,
                operation,
                LocalDateTime.now(),
                username,
                sensors),
            HttpStatus.OK
        );
    }

    private LocalDateTime constructDate(String month, String year) throws IllegalArgumentException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
        String monthYear = month + "-" + year;
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(monthYear, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "the month or year does not match the required pattern (e.g., 'January', '2024')");
        }
        return yearMonth.atDay(1).atStartOfDay();
    }

    private CredentialsDTOWithSensorReqst constructSubmitData(Person person, SubmitDTOReqst submit,
        LocalDateTime date){
        return CredentialsDTOWithSensorReqst.builder()
            .username(person.getUsername())
            .sensor(SensorDTOWithOneReadingReqst.builder()
                .type(submit.getType().toUpperCase().equals(SensorType.HOT_WATER_METERS.toString())
                    ? SensorType.HOT_WATER_METERS : SensorType.COLD_WATER_METERS)
                .reading(ReadingDTO.builder()
                    .date(date)
                    .indication(submit.getReading())
                    .build())
                .build())
            .build();
    }

    private void processPushHistory(Person person, SubmitDTOReqst submit, LocalDateTime date){
        historyService.push(History.builder()
            .action("SUBMIT (" + submit.getType() + ") WITH READINGS: " + submit.getReading()
                + " BY " + date)
            .time(LocalDateTime.now())
            .username(person.getUsername())
            .build());
    }
}