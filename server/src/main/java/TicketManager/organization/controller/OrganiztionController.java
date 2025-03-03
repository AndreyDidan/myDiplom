package TicketManager.organization.controller;

import TicketManager.organization.model.Organization;
import TicketManager.organization.model.OrganizationDto;
import TicketManager.organization.service.OrganizationService;
import TicketManager.user.model.MyUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/organization")
@RequiredArgsConstructor
public class OrganiztionController {
    private final OrganizationService organizationService;
    @Transactional(readOnly = true)
    @GetMapping
    public String organizations(Model model, @AuthenticationPrincipal MyUser users) {

        List<OrganizationDto> organizations = organizationService.getAllOrganization();
        model.addAttribute("organizations", organizations);
        model.addAttribute("title", "Главная страница");
        return "organizations";
    }

    @Transactional(readOnly = true)
    @GetMapping("/add")
    public String organizationAdd(Model model, @AuthenticationPrincipal MyUser users) {
        List<OrganizationDto> organizations = organizationService.getAllOrganization();
        model.addAttribute("organizations", organizations);
        model.addAttribute("title", "Главная страница");
        return "organizations_add";
    }

    @PostMapping("/add")
    public String managerOrganizationAdd(
            @RequestParam String organizationName,
            @RequestParam String littleNameOrganization,
            @RequestParam String nameDirector,
            @RequestParam String surnameDirector,
            @RequestParam Long inn,
            @RequestParam Integer okved,
            @RequestParam String adress,
            @RequestParam String phoneOrganization,
            @RequestParam String emailOrganization,
            Model model, @AuthenticationPrincipal MyUser users) {

        Organization organization = organizationService.addOrganization(organizationName, littleNameOrganization, nameDirector,
        surnameDirector, inn, okved, adress, phoneOrganization, emailOrganization);
        model.addAttribute("title", "Главная страница");
        return "redirect:/organization";
    }

    @Transactional(readOnly = true)
    @GetMapping("/{organizationId}")//вместо blog-main
    public String organizationDetails(@PathVariable(value = "organizationId") Long organizationId, Model model, @AuthenticationPrincipal MyUser users) {
        Organization organization = organizationService.getOrganizationForId(organizationId);
        if (organization == null) {
            return "redirect:/organization";
        }
        model.addAttribute("organization", organization);
        model.addAttribute("title", "Главная страница");
        return "organization_details";
    }

    @RequestMapping(value = "/{organizationId}/remove", method = RequestMethod.POST)
    public String managerOrganizationDelete(
            @PathVariable(value = "organizationId") Long organizationId,
            Model model, @AuthenticationPrincipal MyUser users) {
        organizationService.deleteOrganizationForId(organizationId);
        model.addAttribute("title", "Главная страница");
        return "redirect:/organization";  // Перенаправление на главную страницу организаций
    }

    @PostMapping("/{organizationId}/edit")
    public String managerOrganizationUpdate(
            @PathVariable(value = "organizationId") Long organizationId,
            @Valid Organization updatedOrganization, // Используйте аннотацию @Valid
            BindingResult bindingResult,
            Model model, @AuthenticationPrincipal MyUser users) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Редактирование организации");
            return "organization_edit";
        }

        Organization organization = organizationService.getOrganizationForId(organizationId);
        organization.setOrganizationName(updatedOrganization.getOrganizationName());
        organization.setLittleNameOrganization(updatedOrganization.getLittleNameOrganization());
        organization.setNameDirector(updatedOrganization.getNameDirector());
        organization.setSurnameDirector(updatedOrganization.getSurnameDirector());
        organization.setInn(updatedOrganization.getInn());
        organization.setOkved(updatedOrganization.getOkved());
        organization.setAdress(updatedOrganization.getAdress());
        organization.setPhoneOrganization(updatedOrganization.getPhoneOrganization());
        organization.setEmailOrganization(updatedOrganization.getEmailOrganization());
        organizationService.updateOrganization(organization);
        model.addAttribute("title", "Главная страница");
        return "redirect:/organization";
    }

    @Transactional(readOnly = true)
    @GetMapping("/{organizationId}/edit")//вместо blog-main
    public String organizationEdit(@PathVariable(value = "organizationId") Long organizationId, Model model, @AuthenticationPrincipal MyUser users) {
        Organization organization = organizationService.getOrganizationForId(organizationId);
        if (organization == null) {
            return "redirect:/organization";
        }
        model.addAttribute("organization", organization);
        model.addAttribute("title", "Главная страница");
        return "organization_edit";
    }
}
