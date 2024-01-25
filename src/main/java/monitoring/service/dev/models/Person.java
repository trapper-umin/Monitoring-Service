package monitoring.service.dev.models;

import lombok.*;
import monitoring.service.dev.common.Role;

import java.time.LocalDateTime;

@Getter
@Setter
public class Person {

    private String username;

    private String password;


    private String firstName;

    private String lastName;

    private int age;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Role role;

    private Person(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.role = builder.role;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private int age;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Role role;

        public Person build(){
            return new Person(this);
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder age(int age){
            this.age=age;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }
    }
}
