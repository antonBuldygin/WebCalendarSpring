package webCalendarSpring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import webCalendarSpring.entities.BookEntity;

@Repository
@Component
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
}