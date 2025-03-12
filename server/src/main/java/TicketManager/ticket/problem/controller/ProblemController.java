package TicketManager.ticket.problem.controller;

import TicketManager.organization.department.positions.model.Position;
import TicketManager.systems.functionssystem.controller.FunctionSystemController;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.functionssystem.repository.FunctionSystemRepositry;
import TicketManager.systems.model.System;
import TicketManager.systems.repository.SystemRepository;
import TicketManager.ticket.fuctionsposition.model.FunctionsPosition;
import TicketManager.ticket.fuctionsposition.repository.FunctionsPositionRepository;
import TicketManager.ticket.problem.model.Problem;
import TicketManager.ticket.problem.repository.ProblemRepository;
import TicketManager.user.model.MyUser;
import TicketManager.user.repository.MyUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final FunctionSystemController functionSystemController;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private FunctionSystemRepositry functionSystemRepositry;

    @Autowired
    private FunctionsPositionRepository functionsPositionRepository;

    @Transactional
    @GetMapping//вместо blog-main
    public String problemsPosition(Model model, @AuthenticationPrincipal MyUser user) {
        Position position = user.getPositionId();
        List<Problem> problems = problemRepository.findAllByUserId_PositionId(position);
        //List<Problem> problems = problemRepository.findAll();
        model.addAttribute("problems", problems);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "problems_user";
    }

    @Transactional
    @GetMapping("/all")//вместо blog-main
    public String AllProblems(Model model, @AuthenticationPrincipal MyUser user) {
        List<Problem> problems = problemRepository.findAll();
        model.addAttribute("problems", problems);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "problems";
    }

    @Transactional
    @GetMapping("/add")
    public String problemAdd(Model model, @AuthenticationPrincipal MyUser user) {

        List<String> systemShortNames = systemRepository.findAll().stream()
                .map(System::getLittleNameSystem)
                .collect(Collectors.toList());

        List<FunctionSystem> functionSystems = functionSystemRepositry.findAll();

        List<String> functionSystemShortName = functionSystemRepositry.findAll().stream()
                .map(FunctionSystem::getNameFunctionSystem)
                .collect(Collectors.toList());

        //List<String> systemShortNames = getAllSystemsPosition(functionSystems);

        List<String> problems = problemRepository.findAll().stream()
                .map(Problem::getLitleDescriptionProblem)
                .collect(Collectors.toList());

        model.addAttribute("systemShortNames", systemShortNames);
        model.addAttribute("functionSystemShortName", functionSystemShortName);

        model.addAttribute("problems", problems);
        model.addAttribute("title", "Добавление контракта");
        model.addAttribute("currentUser", user);

        return "problems_add";
    }

    @PostMapping("/add")
    public String problemAddSubmit(@RequestParam String nameSystem,
                                   @RequestParam String nameFunctionSystem,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm") LocalDateTime dateCreatedProblem,
                                   @RequestParam String litleDescriptionProblem,
                                   @RequestParam String descriptionProblem,
                                   @AuthenticationPrincipal MyUser user,
                                   Model model) {


        Optional<FunctionSystem> nameFunct = functionSystemRepositry.findByNameFunctionSystem(nameFunctionSystem);
        if (!nameFunct.isPresent()) {
            model.addAttribute("error", "Функция не найдена");
            return "problems_add";
        }

        Position position = user.getPositionId();

        Optional<FunctionsPosition> functionsPosition1 = functionsPositionRepository
                .findByPositionAndFunctionSystem(position, nameFunctionSystem);


        // Если дата не была указана, установить текущую дату
        if (dateCreatedProblem == null) {
            dateCreatedProblem = LocalDateTime.now();
        }

        if (litleDescriptionProblem == null || descriptionProblem == null) {
            model.addAttribute("error", "поля описания и краткого описания проблемы обязательны " +
                    "для заполнения");
            return "problems_add";
        }

        //Необходимо добавить проверки
        Boolean isRecommendation = false;
        Boolean isContract = false;

        // Создаем новый объект problems с текущей датой, если она не указана
        Problem problem = new Problem(user, dateCreatedProblem, nameFunct.get(), litleDescriptionProblem,
                descriptionProblem, isRecommendation, isContract);

        // Сохраняем проблему
        problemRepository.save(problem);

        return "redirect:/problem/all"; // Перенаправление на главную страницу
    }

    @Transactional
    @GetMapping("/{problemId}")//вместо blog-main
    public String problemFunctionsDetails(@PathVariable(value = "problemId") Long problemId, Model model,
                                          @AuthenticationPrincipal MyUser users) {
        if (!problemRepository.existsById(problemId)) {
            return "redirect:/problem";
        }
        Optional<Problem> problemFunctions = problemRepository.findById(problemId);
        ArrayList<Problem> result = new ArrayList<>();
        problemFunctions.ifPresent(result::add);
        model.addAttribute("problemFunctions", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "problems_details";
    }

    @Transactional
    @PostMapping("/{problemId}/remove")//вместо blog-main
    public String managerFunctionsPosytionDelete(
            @PathVariable(value = "problemId") Long problemId,
            Model model, @AuthenticationPrincipal MyUser users) {
        Problem problemFunction = problemRepository.findById(problemId).orElseThrow();
        problemRepository.delete(problemFunction);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/problem";
    }

    @GetMapping("/functionssystem")
    @ResponseBody
    public List<String> getFunctionsBySysten(@RequestParam String systemShortNames) {
        log.info("Попытка найти функции системы " + "'" + systemShortNames + "'");

        // Получаем систему по ее короткому имени
        Optional<System> systemOpt = systemRepository.findByLittleNameSystem(systemShortNames);
        if (!systemOpt.isPresent()) {
            return Collections.emptyList(); // Если система не найдена, возвращаем пустой список
        }

        System system = systemOpt.get();

        // Получаем все функции для этой системы
        List<FunctionSystem> functionSystems = functionSystemRepositry.findBySystemId(system);

        // Преобразуем функции в список строк
        List<String> functionSystemNames = functionSystems.stream()
                .map(FunctionSystem::getNameFunctionSystem)
                .collect(Collectors.toList());

        log.info("Попытка передать функции " + "'" + functionSystemNames + "'");

        return functionSystemNames; // Возвращаем список строк
    }


}
