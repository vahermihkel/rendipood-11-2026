package ee.magnus.veebipood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ControllerAdviceHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleException(RuntimeException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setTimestammp(new Date());
        errorMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        // errorMessage.setStatus(400)
        errorMessage.setError(e.getMessage());
        //return ResponseEntity.status(400).body(errorMessage)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorMessage);
    }
}
