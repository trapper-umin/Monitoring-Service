package monitoring.service.dev.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.dtos.responses.ErrorMessage;

public class Sandler {

    private final ObjectMapper jackson;

    public Sandler(ObjectMapper jackson) {
        this.jackson = jackson;
        jackson.registerModule(new JavaTimeModule());
    }

    public void sendErrorResponse(HttpServletResponse resp, int status, String errorMessage) {
        try {
            resp.setContentType("application/json");
            jackson.writeValue(resp.getOutputStream(),
                ErrorMessage.builder().status(status).time(LocalDateTime.now()).error(errorMessage)
                    .build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSuccessResponse(HttpServletResponse resp, Object responseObj) {
        try {
            resp.setContentType("application/json");
            jackson.writeValue(resp.getOutputStream(), responseObj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
