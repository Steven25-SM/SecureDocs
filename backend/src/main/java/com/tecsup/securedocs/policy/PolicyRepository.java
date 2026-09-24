package com.tecsup.securedocs.policy;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    boolean existsByNombre(String nombre);
}