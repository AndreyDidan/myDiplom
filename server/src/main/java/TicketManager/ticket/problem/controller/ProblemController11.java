/*package TicketManager.ticket.problem.controller;


import TicketManager.organization.department.positions.model.Position;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/problem")
public class ProblemController11 {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private FunctionSystemRepositry functionSystemRepositry;

    @Autowired
    private FunctionsPositionRepository functionsPositionRepository;

    @Autowired
    private SystemRepository systemRepository;

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

    @GetMapping("/allfunctionpositionuser")
    private List<String> getAllFunctionPosition(MyUser user) {
        Position position = user.getPositionId();

        // Получаем все функции для позиции пользователя
        List<FunctionSystem> functionSystems = functionsPositionRepository.findAllByPositionId(position).stream()
                .flatMap(functionsPosition -> functionsPosition.getFunctionSystems().stream()) // Получаем все функции из списка
                .sorted(Comparator.comparing(FunctionSystem::getNameFunctionSystem)) // Сортируем по имени функции
                .collect(Collectors.toList()); // Собираем в список

        // только названия функций
        List<String> functionSystemShortNames = functionSystems.stream()
                .map(FunctionSystem::getNameFunctionSystem) // Извлекаем только имена
                .collect(Collectors.toList());
        return functionSystemShortNames;
    }


    @GetMapping("/allsystemspositionuser")
    private List<String> getAllSystemsPosition(List<FunctionSystem> functionSystems) {
        // Извлекаем id всех функций
        List<Long> functionIds = functionSystems.stream()
                .map(FunctionSystem::getFunctionId) // Извлекаем id функции
                .collect(Collectors.toList());

        // Получаем уникальные системы по этим функциям
        List<System> uniqueSystems = functionSystemRepositry.findUniqueSystemsByFunctionIds(functionIds);

        // Получаем краткие наименования этих систем
        List<String> systemShortNames = uniqueSystems.stream()
                .map(System::getLittleNameSystem) // Извлекаем краткие наименования систем
                .collect(Collectors.toList()); // Собираем в список кратких наименований систем

        return systemShortNames;
    }

    @Transactional
    @GetMapping("/add")
    public String problemAdd(Model model, @AuthenticationPrincipal MyUser user) {

        List<String> systemShortName = systemRepository.findAll().stream()
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

        model.addAttribute("systemShortName", systemShortName);
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
        Problem problem = new Problem(user, dateCreatedProblem, litleDescriptionProblem,
                descriptionProblem, isRecommendation, isContract);

        // Сохраняем проблему
        problemRepository.save(problem);

        return "redirect:/problem/all"; // Перенаправление на главную страницу
    }

    @Transactional
    @GetMapping("/adduser")
    public String problemAddUser(Model model, @AuthenticationPrincipal MyUser user) {

        List<String> functionSystemShortNames = getAllFunctionPosition(user);

        Position position = user.getPositionId();

        List<FunctionSystem> functionSystems = functionsPositionRepository.findAllByPositionId(position).stream()
                .flatMap(functionsPosition -> functionsPosition.getFunctionSystems().stream()) // Получаем все функции из списка
                .collect(Collectors.toList());


        List<String> systemShortNames = getAllSystemsPosition(functionSystems);

        List<Problem> problems = problemRepository.findAllByUserId_PositionId(position);

        model.addAttribute("systemShortNames", systemShortNames);
        model.addAttribute("functionSystemShortNames", functionSystemShortNames);

        model.addAttribute("problems", problems);
        model.addAttribute("title", "Добавление контракта");
        model.addAttribute("currentUser", user);

        return "problems_add_user";
    }

    @Transactional
    @PostMapping("/adduser")
    public String problemAddSubmitUser(//@RequestParam String nameSystem,
                                   @RequestParam String nameFunctionSystem,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm") LocalDateTime dateCreatedProblem,
                                   @RequestParam String litleDescriptionProblem,
                                   @RequestParam String descriptionProblem,
                                   @AuthenticationPrincipal MyUser user,
                                   Model model) {

        Optional<FunctionSystem> nameFunct = functionSystemRepositry.findByNameFunctionSystem(nameFunctionSystem);
        if (!nameFunct.isPresent()) {
            model.addAttribute("error", "Функция не найдена");
            return "problems_add_user";
        }

        Position position = user.getPositionId();

        Optional<FunctionsPosition> functionsPosition1 = functionsPositionRepository
                .findByPositionAndFunctionSystem(position, nameFunctionSystem);

        FunctionsPosition functionsPosition = functionsPosition1.get();

        // Если дата не была указана, установить текущую дату
        if (dateCreatedProblem == null) {
            dateCreatedProblem = LocalDateTime.now();
        }

        if (litleDescriptionProblem == null || descriptionProblem == null) {
            model.addAttribute("error", "поля описания и краткого описания проблемы обязательны " +
                    "для заполнения");
            return "problems_add_user";
        }

        //Необходимо добавить проверки
        Boolean isRecommendation = false;
        Boolean isContract = false;

        // Создаем новый объект problems с текущей датой, если она не указана
        Problem problem = new Problem(user, functionsPosition, dateCreatedProblem, litleDescriptionProblem,
                descriptionProblem, isRecommendation, isContract);

        // Сохраняем проблему
        problemRepository.save(problem);

        return "redirect:/problem"; // Перенаправление на главную страницу
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


    /*@GetMapping("/allfunctionssystem")
    @ResponseBody
    private List<String> getAllFunctionPosition(@RequestParam String systemShortName) {
        log.info("Попытка найти функции системы " + "'" + systemShortName + "'");
        // Найдем систему по краткому наименованию
        //Optional<System> system = systemRepository.findByLittleNameSystem(systemShortName);

        System system = systemRepository.findByLittleNameSystem(systemShortName).get();

        // Если система не найдена, возвращаем пустой список
        if (system == null) {
            return new ArrayList<>();
        }

        // Получаем все функции, связанные с системой
        List<String> functionSystemShortName = functionSystemRepositry.findBySystemId(system).stream()
                .map(FunctionSystem::getNameFunctionSystem)
                .collect(Collectors.toList());

        return functionSystemShortName;
    }*/
//}