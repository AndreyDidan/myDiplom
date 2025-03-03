package TicketManager.contract.technicalspecification.repository;

import TicketManager.contract.technicalspecification.model.TechnicalSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TechnicalSpecificationRepository extends JpaRepository<TechnicalSpecification, Long> {
    Optional<TechnicalSpecification> findByNameSpecification(String nameSpecification);
    List<TechnicalSpecification> findAll();

    // Добавляем метод для поиска отделов по организации
    //List<TechnicalSpecification> findById(TechnicalSpecification technicalSpecification);
}
