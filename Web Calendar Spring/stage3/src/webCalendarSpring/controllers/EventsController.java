package webCalendarSpring.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webCalendarSpring.entities.EventEntity;
import webCalendarSpring.repositories.EventEntityRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class EventsController {

    private final EventEntityRepository eventEntityRepository;

    public EventsController(EventEntityRepository eventEntityRepository) {
        this.eventEntityRepository = eventEntityRepository;
    }


    @GetMapping("/event/today")
    public ResponseEntity<?> todayEvents() {

        String data = "{\"data\":\"There are no events for today!\"}";


        if (eventEntityRepository.findByDate(LocalDate.now()).isEmpty()) {
            return ResponseEntity.badRequest().body(data);
        }
        return ResponseEntity.ok().body(eventEntityRepository.findByDate(LocalDate.now()));

    }



    @PostMapping("/event")
    public String createEvent(@RequestBody EventEntity event) {

        eventEntityRepository.save(event);
        return "{ \n" +
                "    \"message\": \"The event has been added!\", \n" +
                "    \"event\": " +"\""+ event.getEvent()  +"\""+ ", \n" +
                "    \"date\": "  +"\""+ event.getDate()  +"\""+ " \n" +
                "} ";
    }


    @GetMapping("/event/{id}")
    public ResponseEntity<?> findEventById(@PathVariable long id) {

        Optional<EventEntity> byId = eventEntityRepository.findById(id);
        EventEntity foundById = new EventEntity();
        if (byId.isPresent()) {
            foundById = byId.get();
        }
        if (byId.isEmpty()) {
            return new ResponseEntity<> ("{\n" +
                    "    \"message\": \"The event doesn't exist!\"\n" +
                    "}",HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().body(foundById);
    }

    @GetMapping("/event")
    public ResponseEntity<?> allEventsBetweenTwoDates(@RequestParam(required = false) String start_time,
                                                      @RequestParam(required = false) String end_time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");

        if (start_time == null || end_time == null || start_time == "" || end_time == "") {
            String data = "{\"data\":\"There are no events!\" } ";
            if (eventEntityRepository.findAll().isEmpty()) {
                return ResponseEntity.badRequest().body(data);
            }
            return ResponseEntity.ok().body(eventEntityRepository.findAll());

        }


        LocalDate start = LocalDate.parse(start_time, formatter);
        LocalDate end = LocalDate.parse(end_time, formatter);

        Optional<List<EventEntity>> eventEntity = eventEntityRepository.findbtTwoDates(start, end);
        List<EventEntity> eventEntities = new ArrayList<>();
        if (!eventEntity.isEmpty()) {
            eventEntities = eventEntity.get();
        }
        if (eventEntity.isEmpty() || eventEntities.size() == 0) {
            String data = "{\"data\":\"There are no events!\" } ";

            return ResponseEntity.badRequest().body(data);
        }


        return ResponseEntity.ok().body(eventEntities);
    }

    @DeleteMapping("/event/{id}")
    public ResponseEntity<?> deleteEventById(@PathVariable long id) {
        Optional<EventEntity> byId = eventEntityRepository.findById(id);
        EventEntity foundById = new EventEntity();

        if (byId.isEmpty()) {
            return new ResponseEntity<> ("{\n" +
                    "    \"message\": \"The event doesn't exist!\"\n" +
                    "}",HttpStatus.NOT_FOUND);
        }

        foundById = byId.get();
        eventEntityRepository.deleteById(id);
        return ResponseEntity.ok().body("{\n" +
                "    \"message\": \"The event has been deleted!\"\n" +
                "}");


    }
}
