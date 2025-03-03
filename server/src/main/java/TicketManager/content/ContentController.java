package TicketManager.content;

import TicketManager.ticket.model.Ticket;
import TicketManager.ticket.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import TicketManager.user.model.MyUser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
public class ContentController {

    /*@GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Главная страница");
        return "index";
    }*/
    @Autowired
    private TicketRepository ticketRepository;


    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal MyUser user) {
        List<Ticket> tickets = ticketRepository.findAll(Sort.by(Sort.Order.asc("ticketId")));
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Ticket ticket : tickets) {
            if (ticket.getLimitsId() != null && ticket.getLimitsId().getPenaltysStart() != null) {
                Duration penaltyStart = ticket.getLimitsId().getPenaltysStart();

                // Проверяем: заявка закрылась или нет, и сравниваем с лимитом штрафного времени
                boolean isExpiredNow = false;
                if (ticket.getDateClose() != null) {
                    // Если заявка закрыта, сравниваем дату закрытия с лимитом
                    isExpiredNow = Duration.between(ticket.getDateCreate(), ticket.getDateClose()).compareTo(penaltyStart) > 0;
                } else {
                    // Если заявка не закрыта, сравниваем текущее время с лимитом
                    isExpiredNow = Duration.between(ticket.getDateCreate(), currentDateTime).compareTo(penaltyStart) > 0;
                }

                // Обновляем статус просрочки, если он изменился
                if (!Objects.equals(ticket.getIsExpired(), isExpiredNow)) {
                    ticket.setIsExpired(isExpiredNow);
                    ticketRepository.save(ticket); // Фиксируем изменения
                }
            } else {
                log.warn("Пропущена заявка ID=" + ticket.getTicketId() + ": данные о лимите отсутствуют.");
            }
        }

        // Повторно получаем обновлённый список заявок для отображения в модели
        tickets = ticketRepository.findAll(Sort.by(Sort.Order.asc("ticketId")));
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user);

        return "index";
    }


    @GetMapping("/about")
    public String about(Model model, @AuthenticationPrincipal MyUser user) {
        model.addAttribute("title", "Тестовая страница");
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "about";
    }

    @GetMapping("/tickets")
    public String tickets(Model model, @AuthenticationPrincipal MyUser user) {
        model.addAttribute("title", "Главная страница");
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "tickets";
    }

    /*@GetMapping("/home")
    public String handleWelcome() {
        return "home";
    }*/

    @GetMapping("/login")
    public String handleLogin() {
        return "custom_login";
    }
}