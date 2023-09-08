package webCalendarSpring;

import java.time.LocalDate;

/**
 * DTO for {@link webCalendarSpring.entities.EventEntity}
 */
public class Event {
    String event;
    String date ;

    public Event(String event, String date) {
        this.event = event;
        this.date = date;
    }

    @Override
    public String toString() {
        return "{ \"event\":" + "\""+ event +"\""+
                ", \"date\":" + "\""+ date +"\""+ "}";
    }
}
