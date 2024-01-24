package monitoring.service.dev.models;

import lombok.*;
import monitoring.service.dev.common.Role;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private int id;

    private String username;

    private String password;

    private Role role;
}
