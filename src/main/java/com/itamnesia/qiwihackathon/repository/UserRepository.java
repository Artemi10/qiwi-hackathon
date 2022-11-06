package com.itamnesia.qiwihackathon.repository;

import com.itamnesia.qiwihackathon.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT user
        FROM User user
        WHERE user.login = :login
    """)
    Optional<User> findByLogin(String login);

    default boolean existsByLogin(String login) {
        return findByLogin(login).isPresent();
    }
}
