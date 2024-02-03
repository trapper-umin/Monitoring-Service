package monitoring.service.dev.models;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private int id;

    private String username;

    private String action;

    private LocalDateTime time;
}
