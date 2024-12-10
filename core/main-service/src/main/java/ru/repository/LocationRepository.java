package ru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
