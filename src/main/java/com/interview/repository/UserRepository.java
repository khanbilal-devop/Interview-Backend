package com.interview.repository;

import com.interview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmailAndActiveIsTrue(String email);

    @Query("SELECT count(c) FROM User c where c.id != :id AND c.email = :cpEmail And active = true")
    int countForUniqueEmail(@Param("id") Long id, @Param("cpEmail") String cpEmail);

    @Modifying
    @Query("UPDATE User c SET c.active = false WHERE c.id =:id")
    int delete(@Param("id") Long id);
}
