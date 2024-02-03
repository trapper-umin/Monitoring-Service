package monitoring.service.dev.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Audit {

    private int id;

    private String log;
}