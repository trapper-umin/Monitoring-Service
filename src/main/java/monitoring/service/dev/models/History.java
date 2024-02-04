package monitoring.service.dev.models;

import lombok.*;
import monitoring.service.dev.dtos.requests.CredentialsDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private CredentialsDTO credentials;

    private String action;

    private LocalDateTime time;
}
