package ru.practicum.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u " +
            "FROM Users AS u " +
            "WHERE " +
            "(u.id IN :users OR :users is null)")
    List<User> getAllUsers(List<Long> users, Pageable pageable);
}
