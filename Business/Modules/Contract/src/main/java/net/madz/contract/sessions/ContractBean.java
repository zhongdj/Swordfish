package net.madz.contract.sessions;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.madz.authorization.MultitenancyBean;
import net.madz.authorization.entities.User;
import net.madz.authorization.interceptor.UserSession;
import net.madz.authorization.interceptor.UserSession.SessionBeanAuthorizationInterceptor;
import net.madz.common.entities.Additive;
import net.madz.common.entities.Mixture;
import net.madz.contract.entities.Contract;
import net.madz.contract.entities.PouringPart;
import net.madz.contract.entities.UnitProject;
import net.madz.contract.sessions.Consts.ErrorCodes;
import net.madz.contract.sessions.CreateContractRequest.UnitProjectInfo;
import net.madz.contract.spec.entities.PouringPartSpec;
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
            throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME, ErrorCodes.SERVER_INTERNAL_ERROR, ex);
        } finally {
            em.close();
        }
    }

    public CreatePouringPartSpecResponse createPouringPartSpecs(Long unitProjectId,
            List<CreatePouringPartSpecRequest> request) throws BusinessModuleException {
        EntityManager em = em();
        try {
            if ( null == unitProjectId ) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME, ErrorCodes.UNIT_PROJECT_ID_NULL);
            }
            final UnitProject unitProject = em.find(UnitProject.class, unitProjectId);
            if ( null == unitProject ) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME, ErrorCodes.UNIT_PROJECT_ID_INVALID);
            }
            final List<PouringPartSpec> specList = new LinkedList<PouringPartSpec>();
            for ( CreatePouringPartSpecRequest item : request ) {
                final PouringPartSpec spec = createPouringPartSpec(em, unitProject, item);
                specList.add(spec);
            }
            return generateResponse(specList);
        } finally {
            em.close();
        }
    }

    private CreatePouringPartSpecResponse generateResponse(final List<PouringPartSpec> specList) {
        PouringPartSpecInfo[] pouringPartSpecInfos = new PouringPartSpecInfo[specList.size()];
        for ( int i = 0; i < specList.size(); i++ ) {
            PouringPartSpecInfo info = new PouringPartSpecInfo();
            info.setId(specList.get(i).getId());
            pouringPartSpecInfos[i] = info;
        }
        final CreatePouringPartSpecResponse response = new CreatePouringPartSpecResponse();
        response.setPouringPartSpecInfos(pouringPartSpecInfos);
        return response;
    }

    private PouringPartSpec createPouringPartSpec(EntityManager em, final UnitProject unitProject,
            CreatePouringPartSpecRequest item) throws BusinessModuleException {
        final User user = UserSession.getUserSession().getUser();
        final PouringPartSpec spec = new PouringPartSpec();
        spec.setCreatedBy(user);
        spec.setUpdatedBy(user);
        spec.setCreatedOn(new Date());
        spec.setUnitProject(unitProject);
        // Create pouringPart
        PouringPart pouringPart = new PouringPart();
        pouringPart.setCreatedBy(user);
        pouringPart.setUpdatedBy(user);
        pouringPart.setCreatedOn(new Date());
        final String pouringPartName = item.getPouringPartName();
        if ( null == pouringPartName || 0 >= pouringPartName.length() ) {
            throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME, ErrorCodes.POURING_PART_NAME_EMPTY);
        }
        pouringPart.setName(pouringPartName);
        pouringPart.setComment(item.getPouringPartComment());
        spec.setPouringPart(pouringPart);
        // Find mixture
        Mixture mixture = findMixture(em, item.getMixtureId(), item.getMixtureGradeName());
        spec.setMixture(mixture);
        // Find additives
        List<Additive> additives = new LinkedList<Additive>();
        AdditiveInfo[] additivesInfo = item.getAdditives();
        for ( AdditiveInfo info : additivesInfo ) {
            long id = info.getId();
            String name = info.getName();
            Additive additive = findAdditive(em, id, name);
            additives.add(additive);
        }
        spec.setAdditives(additives);
        em.persist(spec);
        return spec;
    }

    private Mixture findMixture(EntityManager em, long mixtureId, String gradeName) throws BusinessModuleException {
        if ( mixtureId > 0 ) {
            Mixture result = em.find(Mixture.class, mixtureId);
            if ( null == result ) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME,
                        ErrorCodes.MIXTURE_ID_NOT_EXIST, new String[] { String.valueOf(mixtureId) });
            }
            return result;
        } else {
            if ( null == gradeName || 0 > gradeName.trim().length() ) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME,
                        ErrorCodes.MIXTURE_GRADE_NAME_EMPTY);
            }
            try {
                Query query = em.createNamedQuery("Mixture.findByGradeName").setParameter("gradeName", gradeName);
                Mixture result = (Mixture) query.getSingleResult();
                return result;
            } catch (NoResultException ex) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME,
                        ErrorCodes.MIXTURE_NOT_FOUND_WITH_GRADE_NAME, new String[] { gradeName });
            }
        }
    }

    private Additive findAdditive(EntityManager em, long id, String name) throws BusinessModuleException {
        if ( id > 0 ) {
            Additive result = em.find(Additive.class, id);
            if ( null == result ) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME,
                        ErrorCodes.ADDITIVE_ID_INVALID, new String[] { String.valueOf(id) });
            }
            return result;
        } else {
            if ( null == name || 0 >= name.trim().length() ) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME,
                        ErrorCodes.ADDITIVE_NAME_EMPTY);
            }
            try {
                Query query = em.createNamedQuery("Additive.findByName").setParameter("name", name);
                Additive result = (Additive) query.getSingleResult();
                return result;
            } catch (NoResultException ex) {
                throw new BusinessModuleException(ContractBean.class, Consts.MODULE_NAME,
                        ErrorCodes.ADDITIVE_NAME_INVALID);
            }
        }
    }
}
