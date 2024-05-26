package com.pw.springsecurity.repository;

import com.pw.springsecurity.model.entities.Role;
import com.pw.springsecurity.model.entities.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
}
