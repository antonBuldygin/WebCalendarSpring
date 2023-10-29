package webCalendarSpring.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import webCalendarSpring.entities.EventEntity;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EventsController {

    private final List<EventEntity>  eventEntityRepository = new ArrayList<>();

    @PostMapping("/event")
    public ResponseEntity createEvent( @RequestBody EventEntity event) {

        if(event.getDate() == null ){return new ResponseEntity<>( HttpStatus.BAD_REQUEST);}
        if( event.getEvent()== null){return new ResponseEntity<>( HttpStatus.BAD_REQUEST);}
        if(event.getEvent().trim().isEmpty()){return new ResponseEntity<>( HttpStatus.BAD_REQUEST);}
        eventEntityRepository.add(event);
       String body = "{ \n" +
                "    \"message\": \"The event has been added!\", \n" +
                "    \"event\": " +"\""+ event.getEvent()  +"\""+ ", \n" +
                "    \"date\": "  +"\""+ event.getDate()  +"\""+ " \n" +
                "} ";
       return new ResponseEntity<>( body, HttpStatus.OK);
    }

}
