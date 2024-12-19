package ru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.model.UserRequest;
import ru.related.RequestStatus;

import java.util.List;
import java.util.Set;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {

    Long countByStatusAndEvent(RequestStatus status, Long id);

    List<UserRequest> findAllByRequester(Long id);

    List<UserRequest> findAllByRequesterAndEvent(Long userId, Long eventId);

    List<UserRequest> findAllByEvent(Long eventId);

    List<UserRequest> findByIdInAndEvent(Set<Long> ids, Long eventId);


    @Modifying
    @Query(value = "UPDATE user_request SET status = ?1 WHERE id = ?2 ", nativeQuery = true)
    void updateUserRequestStatus(String status, Long id);

    @Modifying
    @Query(value = "UPDATE user_request SET status = 'CANCELED' WHERE event_id = ?1 ", nativeQuery = true)
    void cancelStatusAllRequestPending(Long eventId);

}
