package ru.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.model.Event;
import ru.related.EventState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findByInitiator(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator(Long id, Long initiatorId);

    boolean existsByCategory(Long catId);

    Set<Event> findByIdIn(Set<Long> ids);

    List<Event> findByStateIsAndInitiatorIn(EventState state, Set<Long> initiatorIds);
}
