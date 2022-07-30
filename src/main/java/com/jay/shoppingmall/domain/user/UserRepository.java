package com.jay.shoppingmall.domain.user;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByEmailContaining(@Param("q") String email);

    @Query("SELECT u.phoneNumber FROM User u WHERE u.phoneNumber.first = :first AND u.phoneNumber.middle = :middle AND u.phoneNumber.last = :last")
    Optional<User> findByPhoneNumber(String first, String middle, String last);
}
