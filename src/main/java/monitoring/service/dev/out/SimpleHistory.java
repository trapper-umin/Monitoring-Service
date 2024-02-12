package monitoring.service.dev.out;

import monitoring.service.dev.controllers.DoController;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.History;

import java.time.LocalDateTime;

public class SimpleHistory {

    private static SimpleHistory instance;

    private SimpleHistory() {
    }

    public static SimpleHistory getInstance() {
        if (instance == null) {
            instance = new SimpleHistory();
        }
        return instance;
    }

    private static final DoController doController = DoController.getInstance();

    public void push(CredentialsDTOResp credentials) {
        String username = credentials.getUsername();
        doController.pushHistory(History.builder().username(username).action(
                "SUBMIT (" + credentials.getSensors().get(0).getType() + ") WITH READINGS: "
                    + credentials.getSensors().get(0).getReadings().get(0).getIndication() + " BY "
                    + credentials.getSensors().get(0).getReadings().get(0).getDate())
            .time(LocalDateTime.now()).build());
    }
}
