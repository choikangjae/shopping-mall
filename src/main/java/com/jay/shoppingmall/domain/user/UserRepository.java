package com.jay.shoppingmall.domain.user;

import com.jay.shoppingmall.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<List<User>> findByEmailContaining(@Param("q") String email);

}
