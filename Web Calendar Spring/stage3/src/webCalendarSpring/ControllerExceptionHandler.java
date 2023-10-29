package webCalendarSpring;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

//@ControllerAdvice

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler  {

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {


        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("status", status.value());
        body.put("timestamp", LocalDateTime.now());
//        body.put("exception", ex.getClass());
        return new ResponseEntity<>(  headers, status);
    }
}






