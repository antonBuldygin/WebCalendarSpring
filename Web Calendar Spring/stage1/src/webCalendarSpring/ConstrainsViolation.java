package webCalendarSpring;

import com.google.gson.Gson;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

@ControllerAdvice
public class ConstrainsViolation {
    private static final Gson gson = new Gson();

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<?> eventConstrainsViolation(ConstraintViolationException ex) {

        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
//        final List<Violation> violations = ex.getConstraintViolations().stream()
//                       .map(
//                        violation -> new Violation(
//                                violation.getPropertyPath().toString(),
//                                violation.getMessage()
//                        )
//                )
//                .collect(Collectors.toList());

        Map<String, Map<String,String>> result = new TreeMap<>();
        for (ConstraintViolation<?> it :
                constraintViolations) {


            if (it.getPropertyPath().toString().equals("date")) {

//                String json = gson.toJson(Map.of(
//                        "message", (Map.of("date","The event date with the correct format is required! The correct format is YYYY-MM-DD!"
//                ))));

                result.put("message", Map.of("date",
                        "The event date with the correct format is required! The correct format is YYYY-MM-DD!"));



            }

            if (it.getPropertyPath().toString().equals("event")) {

                result.put("message", Map.of("event", "The event name is required!"));



            }

        }

//        result.stream().distinct().toList();
        return ResponseEntity.badRequest().body(result);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String eventNotReadableException(HttpMessageNotReadableException ex) {

        return ex.getMessage();
    }


    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String eventException(DataIntegrityViolationException ex) {

        return ex.getMessage();
    }
}


