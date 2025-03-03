/*package TicketManager.ticket.comment.controller;

import TicketManager.contract.limit_contract.model.Limit;
import TicketManager.contract.model.Contract;
import TicketManager.organization.model.Organization;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.model.System;
import TicketManager.ticket.comment.model.CommentsTickets;
import TicketManager.ticket.comment.repository.CommentRepository;
import TicketManager.ticket.model.Status;
import TicketManager.ticket.model.Ticket;
import TicketManager.ticket.problem.model.Problem;
import TicketManager.ticket.repository.TicketRepository;
import TicketManager.user.model.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/comment")
public class CommentTicketsController {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/{ticketId}/add")
    public String commentAdd(@PathVariable(value = "ticketId") Long ticketId, Model model, @AuthenticationPrincipal MyUser user) {

        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        List<CommentsTickets> commentsTickets = commentRepository.findByTicketIdOrderByCreatedAsc(ticket.get());

        model.addAttribute("commentsTickets", commentsTickets);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "tickets_add";
    }

    @Transactional
    @PostMapping("/{ticketId}/add")//вместо blog-main
    public String ManagerCommentAdd(
            @AuthenticationPrincipal MyUser user,
            @PathVariable(value = "ticketId") Long ticketId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm") LocalDateTime dateCreated,
            @RequestParam String text,
            Model model) {

        if (dateCreated == null) {
            dateCreated = LocalDateTime.now();
        }

        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        CommentsTickets commentsTickets = new CommentsTickets(user, text, ticket.get(), dateCreated);

        commentRepository.save(commentsTickets);

        return "redirect:/ticket/{ticketId}";
    }
}*/