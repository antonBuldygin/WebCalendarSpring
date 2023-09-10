package webCalendarSpring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import webCalendarSpring.entities.EventEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventEntityRepository extends JpaRepository<EventEntity, Long> {
    @Query("select e from EventEntity e where e.date >= ?1 and e.date <= ?2")
    Optional<List<EventEntity>> findbtTwoDates(@NonNull LocalDate date, @NonNull LocalDate date1);
    List<EventEntity> findByDate(@NonNull LocalDate date);

    @Override
    Optional<EventEntity> findById(Long aLong);


}