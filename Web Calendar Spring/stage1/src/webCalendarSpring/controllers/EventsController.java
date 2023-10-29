package webCalendarSpring.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class EventsController {


    @GetMapping("/event/today")
    public ResponseEntity<?> todayEvents() {

       List< Map<String,String>> response = new ArrayList<>();

        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}
