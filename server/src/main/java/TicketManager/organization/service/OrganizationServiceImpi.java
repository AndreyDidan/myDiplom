package TicketManager.organization.service;

import TicketManager.exception.NotFoundException;
import TicketManager.exception.ValidationException;
import TicketManager.organization.OrganizationMapper;
import TicketManager.organization.model.Organization;
import TicketManager.organization.model.OrganizationDto;
import TicketManager.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpi implements OrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    @Override
    public List<OrganizationDto> getAllOrganization() {
        List<OrganizationDto> organizations = organizationRepository.findAll().stream()
                .map(OrganizationMapper::mapToOrganization).toList();
        return organizations;
    }

    @Transactional(readOnly = true)
    @Override
    public Organization getForLittleNameOrganization(String littleNameOrganization) {

        log.info("Запрос на получение организации по краткому наименованию");
        Organization organization = organizationRepository.findByLittleNameOrganization(littleNameOrganization)
                .orElseThrow(() -> new NotFoundException("Организация " + littleNameOrganization + " не найдена."));
        return organization;
    }

    @Transactional(readOnly = true)
    @Override
    public Organization getOrganizationForId(Long organizationId) {

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() ->
                new NotFoundException("Организация с organizationId = " + organizationId + " не найдена."));;
        return organization;
    }

    @Override
    public Organization addOrganization(String organizationName, String littleNameOrganization, String nameDirector,
                                        String surnameDirector, Long inn, Integer okved, String adress,
                                        String phoneOrganization, String emailOrganization) {

        if (organizationRepository.findByLittleNameOrganization(littleNameOrganization).isPresent()) {
            throw new ValidationException("Организация " + littleNameOrganization + " уже существует.");
        }
        Organization organization = new Organization(organizationName, littleNameOrganization, nameDirector,
                surnameDirector, inn, okved, adress, phoneOrganization, emailOrganization);
        organizationRepository.save(organization);
        return organization;
    }

    @Override
    public Organization updateOrganization(Organization updatedOrganization) {
        Organization organization = new Organization();
        organization.setOrganizationId(updatedOrganization.getOrganizationId());
        organization.setOrganizationName(updatedOrganization.getOrganizationName());

        if (organizationRepository.findByLittleNameOrganization(updatedOrganization.getLittleNameOrganization()).get()
                .getOrganizationId() != updatedOrganization.getOrganizationId()) {
            throw new ValidationException("Организация " + updatedOrganization.getLittleNameOrganization() + " уже существует.");
        }
        organization.setLittleNameOrganization(updatedOrganization.getLittleNameOrganization());
        organization.setNameDirector(updatedOrganization.getNameDirector());
        organization.setInn(updatedOrganization.getInn());
        organization.setOkved(updatedOrganization.getOkved());
        organization.setAdress(updatedOrganization.getAdress());
        organization.setPhoneOrganization(updatedOrganization.getPhoneOrganization());
        organization.setEmailOrganization(updatedOrganization.getEmailOrganization());
        organizationRepository.save(organization);
        return organization;
    }

    @Override
    public void deleteOrganizationForLittleName(String littleNameOrganization) {

        Organization organization = organizationRepository.findByLittleNameOrganization(littleNameOrganization).
                orElseThrow(() -> new NotFoundException("Организация " + littleNameOrganization + " не найдена."));
        organizationRepository.deleteById(organization.getOrganizationId());
    }

    @Override
    public void deleteOrganizationForId(Long organizationId) {
        log.info("Находим организацию по id");
        Organization organization = organizationRepository.findById(organizationId).
                orElseThrow(() -> new NotFoundException("Организация с id = " + organizationId + " не найдена."));
        log.info("Пытаемся удалить организацию по id");
        organizationRepository.deleteById(organization.getOrganizationId());
    }
}
