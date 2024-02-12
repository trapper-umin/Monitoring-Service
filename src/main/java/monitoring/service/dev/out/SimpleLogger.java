package monitoring.service.dev.out;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.controllers.AdminController;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.models.Person;

import java.time.LocalDateTime;

public class SimpleLogger {

    private static SimpleLogger instance;

    private SimpleLogger(){}

    public static SimpleLogger getInstance(){
        if(instance==null){
            instance = new SimpleLogger();
        }
        return instance;
    }

    private static final AdminController adminController = AdminController.getInstance();

    public void logEventRegLogIn(String action, Person person){
        adminController.postAudit(Audit.builder()
                .log("["+ LocalDateTime.now()+"] " + action + (person!=null ?
                        " SUCCESS\n - username: " + person.getUsername() : "ERROR"))
                .build());
    }

    public void LogEventUsername(String action, CredentialsDTOResp credentials){
        adminController.postAudit(Audit.builder()
                .log("["+LocalDateTime.now()+"]" + action + " SUCCESS" +
                        "\n - username: "+credentials.getUsername())
                .build());
    }

    public void logEventUsernameAndError(String action, CredentialsDTOResp credentials, String message){
        adminController.postAudit(Audit.builder()
                .log("["+LocalDateTime.now()+"]" + action + " ERROR" +
                        "\n - username: "+credentials.getUsername()+
                        "\n - error: "+message)
                .build());
    }

    public void logEventUsernameRoleAndError(String action, CredentialsDTOResp credentials, String message, Role role){
        adminController.postAudit(Audit.builder()
                .log("["+LocalDateTime.now()+"]" + action + " ERROR" +
                        "\n - username: "+credentials.getUsername()+
                        "\n - role: "+credentials.getRole()+
                        "\n - error: "+message)
                .build());
    }


    public void logEventSubmitSuccess(CredentialsDTOResp credentials){
        adminController.postAudit(Audit.builder()
                .log("["+LocalDateTime.now()+"] SUBMIT SUCCESS" +
                        "\n - username: "+credentials.getUsername()+
                        "\n - sensor: "+credentials.getSensors().get(0).getType()+
                        "\n - readings: "+credentials.getSensors().get(0).getReadings().get(0).getIndication())
                .build());
    }


    public void logEventRightsToAdminSuccess(CredentialsDTOResp credentials, String username){
        adminController.postAudit(Audit.builder()
                .log("["+LocalDateTime.now()+"] RIGHTS SUCCESS" +
                        "\n - username: "+credentials.getUsername()+
                        "\n - role: "+credentials.getRole()+
                        "\n - action: "+username+" -> ADMIN")
                .build());
    }

    public void logEventRightsToUserSuccess(CredentialsDTOResp credentials, String username){
        adminController.postAudit(Audit.builder()
                .log("["+LocalDateTime.now()+"] RIGHTS SUCCESS" +
                        "\n - username: "+credentials.getUsername()+
                        "\n - role: "+credentials.getRole()+
                        "\n - action: "+username+" -> USER")
                .build());
    }

    public void logEventAuditSuccess(CredentialsDTOResp credentials){
        adminController.postAudit(Audit.builder()
                .log("["+LocalDateTime.now()+"] AUDIT SUCCESS" +
                        "\n - username: "+credentials.getUsername()+
                        "\n - role: "+credentials.getRole())
                .build());
    }
}
