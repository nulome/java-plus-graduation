package ru.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.related.RequestStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_request")
public class UserRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false)
    private Long event;
    @Column(name = "requester_id", nullable = false)
    private Long requester;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
