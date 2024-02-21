package monitoring.service.dev.controllers.v2.impl;

import java.util.List;
import monitoring.service.dev.controllers.v2.interfaces.IDoController;
import monitoring.service.dev.dtos.requests.SubmitDTOReqst;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.dtos.responses.HistoryDTOResp;
import monitoring.service.dev.dtos.responses.PersonWithSensorsAndReadingsDTOResp;
import monitoring.service.dev.services.logic.DoLogicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public abstract class ImplDoController implements IDoController {

    private final DoLogicService service;

    public ImplDoController(DoLogicService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<PersonWithSensorsAndReadingsDTOResp> getCurrentReadings(String token) {
        return service.getCurrentReadings(token);
    }

    @Override
    public ResponseEntity<PersonWithSensorsAndReadingsDTOResp> getMonthlyReadings(String token,
        String month, String year) {
        return service.getMonthlyReadings(token, month, year);
    }

    @Override
    public ResponseEntity<WrapperResp<HistoryDTOResp>> getHistory(String token) {
        return service.getHistory(token);
    }

    @Override
    public ResponseEntity<HttpStatus> submitReading(String token, SubmitDTOReqst submit,
        BindingResult bindingResult) {
        return service.submitReading(token, submit, bindingResult);
    }
}
