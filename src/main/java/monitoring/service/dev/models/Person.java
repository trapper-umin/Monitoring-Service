package monitoring.service.dev.models;

import lombok.*;
import monitoring.service.dev.common.Role;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private int id;

    private String username;

    private String password;

    private List<Sensor> sensors;

    private Role role;
}
