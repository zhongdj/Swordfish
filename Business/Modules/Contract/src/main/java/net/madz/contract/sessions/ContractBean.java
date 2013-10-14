package net.madz.contract.sessions;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import net.madz.authorization.MultitenancyBean;
import net.madz.authorization.interceptor.UserSession;
import net.madz.authorization.interceptor.UserSession.SessionBeanAuthorizationInterceptor;
import net.madz.contract.entities.Contract;
import net.madz.contract.entities.UnitProject;
import net.madz.contract.sessions.CreateContractRequest.UnitProjectInfo;
import net.madz.customer.entities.Contact;
import net.madz.customer.entities.CustomerAccount;
import net.madz.utils.BusinessModuleException;

@Stateless
@LocalBean
@Interceptors(SessionBeanAuthorizationInterceptor.class)
@RolesAllowed({ "admin", "sales" })
public class ContractBean extends MultitenancyBean {

    public CreateContractResponse createContract(CreateContractRequest request) throws BusinessModuleException {
        final Date justNow = new Date();
        final CustomerAccount account;
        final EntityManager em = em();
        try {
            if ( 0 < request.getCustomerId() ) {
                account = em.find(CustomerAccount.class, request.getCustomerId());
            } else {
                account = new CustomerAccount();
                account.setFullName(request.getCustomerFullName());
                account.setShortName(request.getCustomerShortName());
                account.setCreatedBy(UserSession.getUserSession().getUser());
                account.setUpdatedBy(UserSession.getUserSession().getUser());
                account.setCreatedOn(justNow);
                account.setUpdatedOn(justNow);
                em.persist(account);
            }
            
            final Contract contract = new Contract();
            contract.setName(request.getContractName());
            contract.setStartDate(request.getContractStartDate());
            contract.setEndDate(request.getContractEndDate());
            contract.setCustomer(account);
            contract.setCreatedBy(UserSession.getUserSession().getUser());
            contract.setUpdatedBy(UserSession.getUserSession().getUser());
            contract.setCreatedOn(justNow);
            contract.setUpdatedOn(justNow);
            em.persist(contract);
            
            ArrayList<UnitProject> result = new ArrayList<>();
            for ( UnitProjectInfo project : request.getUnitProjects() ) {
                final Contact contact = new Contact();
                contact.setName(project.getContactName());
                contact.setBirthYear(project.getContactBirthYear());
                contact.setMale(project.isContactMale());
                contact.setEmail(project.getContactEmail());
                contact.setCellPhone(project.getContactCellPhone());
                contact.setHomePhone(project.getHomePhone());
                contact.setOfficePhone(project.getOfficePhone());
                contact.setWorkCellPhone(project.getWorkCellPhone());
                contact.setCreatedBy(UserSession.getUserSession().getUser());
                contact.setUpdatedBy(UserSession.getUserSession().getUser());
                contact.setCreatedOn(justNow);
                contact.setUpdatedOn(justNow);
                em.persist(contact);
                
                final UnitProject unitProject = new UnitProject();
                unitProject.setAddress(project.getAddress());
                unitProject.setContact(contact);
                unitProject.setContract(contract);
                unitProject.setName(project.getName());
                unitProject.setCreatedBy(UserSession.getUserSession().getUser());
                unitProject.setUpdatedBy(UserSession.getUserSession().getUser());
                unitProject.setCreatedOn(justNow);
                unitProject.setUpdatedOn(justNow);
                em.persist(unitProject);
                result.add(unitProject);
            }
            final Long[] unitProjectIds = new Long[result.size()];
            for ( int i = 0; i < unitProjectIds.length; i++ ) {
                unitProjectIds[i] = result.get(i).getId();
            }
            final CreateContractResponse response = new CreateContractResponse();
            response.setContractId(contract.getId());
            response.setCustomerId(account.getId());
            response.setUnitProjectIds(unitProjectIds);
            return response;
        } catch (Exception ex) {
            throw new BusinessModuleException(ContractBean.class, "contact", "");
        } finally {
            em.close();
        }
    }

    public void createPouringPartSpecs(CreatePouringPartRequest request) {
    }
}
