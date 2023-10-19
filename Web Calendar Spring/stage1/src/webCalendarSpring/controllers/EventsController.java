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
//        JsonObject convertedObject = new Gson().fromJson(data, JsonObject.class);

//        if (eventEntityRepository.findByDate(LocalDate.now()).isEmpty()) {
//            return ResponseEntity.badRequest().body(data);
//        }
        return ResponseEntity.ok().body(eventEntityRepository.findByDate(LocalDate.now()));

    }




}
