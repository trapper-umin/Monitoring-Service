package monitoring.service.dev.controllers.v2.interfaces;

import jakarta.validation.Valid;
import java.util.List;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.dtos.requests.SubmitDTOReqst;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.dtos.responses.HistoryDTOResp;
import monitoring.service.dev.dtos.responses.PersonWithSensorsAndReadingsDTOResp;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface IDoController {

    @GetMapping(value = AppConstants.COMMAND_CURRENT, produces = MediaType.APPLICATION_JSON_VALUE,
        headers = {"Authorization"})
    ResponseEntity<PersonWithSensorsAndReadingsDTOResp> getCurrentReadings(
        @RequestHeader("Authorization") String token
    );

    @GetMapping(value = AppConstants.COMMAND_GET_MONTHLY, produces = MediaType.APPLICATION_JSON_VALUE,
        headers = {"Authorization"}, params = {"month", "year"})
    ResponseEntity<PersonWithSensorsAndReadingsDTOResp> getMonthlyReadings(
        @RequestHeader("Authorization") String token,
        @RequestParam("month") String month,
        @RequestParam("year") String year
    );

    @GetMapping(value = AppConstants.COMMAND_HISTORY, produces = MediaType.APPLICATION_JSON_VALUE,
        headers = {"Authorization"})
    ResponseEntity<WrapperResp<HistoryDTOResp>> getHistory(
        @RequestHeader("Authorization") String token
    );

    @PostMapping(value = AppConstants.COMMAND_SUBMIT, produces = MediaType.APPLICATION_JSON_VALUE,
        headers = {"Authorization"})
    ResponseEntity<HttpStatus> submitReading(
        @RequestHeader("Authorization") String token,
        @RequestBody @Valid SubmitDTOReqst submit, BindingResult bindingResult
    );
}