package TicketManager.ticket.problem.repository;

import TicketManager.organization.department.positions.model.Position;
import TicketManager.ticket.fuctionsposition.model.FunctionsPosition;
import TicketManager.ticket.problem.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findAll();
    List<Problem> findAllByUserId_PositionId(Position position);
    Optional<Problem> findByLitleDescriptionProblem(String litleDescriptionProblem);
}