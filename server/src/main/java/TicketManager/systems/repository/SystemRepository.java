package TicketManager.systems.repository;

import TicketManager.systems.model.System;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemRepository extends JpaRepository<System, Long> {
    Optional<System> findByNameSystem(String nameSystem);
    //System findByLittleNameSystem(String systemShortName);
    Optional<System> findByLittleNameSystem(String littleNameSystem);
    List<System> findAll();
}