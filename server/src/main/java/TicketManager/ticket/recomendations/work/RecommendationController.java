package TicketManager.ticket.recomendations.work;

import TicketManager.contract.model.Contract;
import TicketManager.ticket.recomendations.work.model.RecomendationsWork;
import TicketManager.ticket.recomendations.work.repository.RecomendationsWorkRepository;
import TicketManager.user.model.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired
    private RecomendationsWorkRepository recomendationsWorkRepository;


    @GetMapping//вместо blog-main
    public String contracts(Model model, @AuthenticationPrincipal MyUser user) {
        List<RecomendationsWork> recomendationsWorks = recomendationsWorkRepository.findAll();
        model.addAttribute("recomendationsWorks", recomendationsWorks);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "recommendations";
    }


}
