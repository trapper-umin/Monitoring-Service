package monitoring.service.dev.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reading {

    private double indication;

    private LocalDateTime date;
}
