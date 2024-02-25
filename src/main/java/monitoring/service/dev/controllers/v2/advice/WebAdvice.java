package monitoring.service.dev.controllers.v2.advice;

import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.dtos.responses.ErrorWithOneFieldMessage;
import monitoring.service.dev.dtos.responses.ValidationErrorsDTO;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import monitoring.service.dev.utils.exceptions.FieldsNotValidException;
import monitoring.service.dev.utils.exceptions.ForbiddenException;
import monitoring.service.dev.utils.exceptions.JWTException;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebAdvice {

    @ExceptionHandler(Exception.class)
    private ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleMessage(Exception e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProblemWithSQLException.class)
    private ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleException(
        ProblemWithSQLException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleException(
        NotFoundException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_BAD_REQUEST, "BAD_REQUEST",
                LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidException.class)
    private ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleException(
        NotValidException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_BAD_REQUEST, "BAD_REQUEST",
                LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FieldsNotValidException.class)
    private ResponseEntity<WrapperResp<ValidationErrorsDTO>> handleException(
        FieldsNotValidException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_BAD_REQUEST, "BAD_REQUEST",
                LocalDateTime.now(), e.getErrors()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleException(
        IllegalArgumentException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_BAD_REQUEST, "BAD_REQUEST",
                LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CanNotDoException.class)
    private ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleException(
        CanNotDoException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_BAD_REQUEST, "BAD_REQUEST",
                LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JWTException.class)
    ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleException(JWTException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ForbiddenException.class)
    ResponseEntity<WrapperResp<ErrorWithOneFieldMessage>> handleException(ForbiddenException e) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", LocalDateTime.now(),
                List.of(ErrorWithOneFieldMessage.builder().error(e.getMessage()).build())),
            HttpStatus.FORBIDDEN);
    }
}