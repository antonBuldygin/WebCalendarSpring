package webCalendarSpring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "event_entity")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank( message = "Не дожен быть пустым")
    @NotEmpty( message = "Не должен быть без ничего")
    @NotNull( message = "Не должен быть Null")
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "event")
    private String event;


    @NotNull( message = "Не должен быть Null")
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "date", nullable = false)
    private LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                '}';
    }
}