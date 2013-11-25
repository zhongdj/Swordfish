/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.scheduling.sessions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import net.madz.binding.TransferObjectFactory;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.core.exceptions.AppServiceException;
import net.madz.core.exceptions.BONotFoundException;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.entities.ConcreteTruck;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.entities.MixingPlant;
import net.madz.scheduling.entities.MixingPlantResource;
import net.madz.scheduling.entities.ServiceSummaryPlan;
import net.madz.scheduling.sessions.Consts.ErrorCodes;
import net.madz.scheduling.to.CreateConcreteTruckResourceRequest;
import net.madz.scheduling.to.CreateConcreteTruckResourceResponse;
import net.madz.scheduling.to.CreateMixingPlantResourceRequest;
import net.madz.scheduling.to.CreateMixingPlantResourceResponse;
import net.madz.scheduling.to.CreateServiceSummaryPlanRequest;
import net.madz.scheduling.to.CreateServiceSummaryPlanResponse;
import net.madz.scheduling.to.ServiceOrderTO;

/**
 * 
 * @author Barry
 */
@Stateless
@LocalBean
@Interceptors(SessionBeanAuthorizationInterceptor.class)
public class OperationBean extends MultitenancyBean {

    private static final String SCHEDULING = "scheduling";
    public static Logger logger = Logger.getLogger(OperationBean.class.getName());

    public List<PouringPartSpec> listMyPartsInConstructing() {
        final ArrayList<PouringPartSpec> result = new ArrayList<>();
        return result;
    }

    // public ServiceOrderTO allocateResourceTo(Long summaryPlanId, Long
    // mixingPlantResourceId,
    // Long concreteTruckResourceId, double volume) throws AppServiceException {
    // EntityManager em = em();
    // try {
    // final IServiceSummaryPlan summaryPlan =
    // em.find(IServiceSummaryPlan.class, summaryPlanId);
    // if ( null == summaryPlan ) {
    // throw new BONotFoundException(OperationBean.class, SCHEDULING,
    // ErrorCodes.SUMMARY_PLAN_ID_INVALID);
    // }
    // final IMixingPlantResource plantResource =
    // em.find(IMixingPlantResource.class, mixingPlantResourceId);
    // if ( null == plantResource ) {
    // throw new BONotFoundException(OperationBean.class, SCHEDULING,
    // ErrorCodes.MIXING_PLANT_RESOURCE_ID_INVALID);
    // }
    // final IConcreteTruckResource truckResource =
    // em.find(IConcreteTruckResource.class, concreteTruckResourceId);
    // if ( null == truckResource ) {
    // throw new BONotFoundException(OperationBean.class, SCHEDULING,
    // ErrorCodes.TRUCK_RESOURCE_ID_INVALID);
    // }
    // final IServiceOrder serviceOrderBO =
    // summaryPlan.createServiceOrder(plantResource, truckResource, volume);
    // serviceOrderBO.persist(em);
    // try {
    // return TransferObjectFactory.createTransferObject(ServiceOrderTO.class,
    // serviceOrderBO.get());
    // } catch (Exception e) {
    // logger.log(Level.SEVERE, "TOBindingError", e);
    // throw new AppServiceException(OperationBean.class, SCHEDULING,
    // SCHEDULING,
    // ErrorCodes.SERVER_INTENAL_ERROR, e);
    // }
    // } finally {
    // em.close();
    // }
    // }
    public ServiceOrderTO allocateResourceTo(Long summaryPlanId, Long mixingPlantResourceId, Long concreteTruckResourceId, double volume)
            throws AppServiceException {
        EntityManager em = em();
        try {
            final ServiceSummaryPlan summaryPlan = em.find(ServiceSummaryPlan.class, summaryPlanId);
            if ( null == summaryPlan ) {
                throw new BONotFoundException(OperationBean.class, SCHEDULING, ErrorCodes.SUMMARY_PLAN_ID_INVALID);
            }
            final MixingPlantResource plantResource = em.find(MixingPlantResource.class, mixingPlantResourceId);
            if ( null == plantResource ) {
                throw new BONotFoundException(OperationBean.class, SCHEDULING, ErrorCodes.MIXING_PLANT_RESOURCE_ID_INVALID);
            }
            final ConcreteTruckResource truckResource = em.find(ConcreteTruckResource.class, concreteTruckResourceId);
            if ( null == truckResource ) {
                throw new BONotFoundException(OperationBean.class, SCHEDULING, ErrorCodes.TRUCK_RESOURCE_ID_INVALID);
            }
            final IServiceOrder serviceOrder = summaryPlan.createServiceOrder(plantResource, truckResource, volume);
            em.persist(serviceOrder);
            try {
                return TransferObjectFactory.createTransferObject(ServiceOrderTO.class, serviceOrder);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "TOBindingError", e);
                throw new AppServiceException(OperationBean.class, SCHEDULING, SCHEDULING, ErrorCodes.SERVER_INTENAL_ERROR, e);
            }
        } finally {
            em.close();
        }
    }

    public List<PouringPartSpec> filterMyPartsInConstructing(String filter) {
        return listMyPartsInConstructing();
    }

    public CreateMixingPlantResourceResponse createPlantResource(CreateMixingPlantResourceRequest request) throws AppServiceException {
        EntityManager em = em();
        final String mixingPlantName = request.getMixingPlantName();
        final String operatorName = request.getOperatorName();
        try {
            if ( null == mixingPlantName || 0 >= mixingPlantName.trim().length() ) {
                throw new NullPointerException("MixingPlant resource information is incomplete, you must specify a name for a mixing plant resource");
            }
            if ( null == operatorName || 0 >= operatorName.trim().length() ) {
                throw new NullPointerException("operator name is null.");
            }
            try {
                Query query = em.createNamedQuery("MixingPlantResource.findByPlantName").setParameter("mixingPlantName", mixingPlantName);
                MixingPlantResource result = (net.madz.scheduling.entities.MixingPlantResource) query.getSingleResult();
                if ( null != result ) {
                    throw new IllegalStateException(
                            "The mixing plant resource is already exists. You can create only a mixing plant resource for a mixing plant.");
                }
            } catch (NoResultException expected) {
                // Ignored
            }
            User operator = null;
            try {
                Query query = em.createNamedQuery("User.findByUsername").setParameter("username", operatorName);
                operator = (User) query.getSingleResult();
            } catch (NoResultException e) {
                throw new IllegalStateException("Operator name is not valid.  Please specify an existing operator name. ");
            }
            MixingPlantResource plantResource = new MixingPlantResource();
            User user = UserSession.getUserSession().getUser();
            plantResource.setCreatedBy(user);
            plantResource.setUpdatedBy(user);
            MixingPlant mixingPlant = new MixingPlant();
            mixingPlant.setCreatedBy(user);
            mixingPlant.setName(mixingPlantName);
            mixingPlant.setUpdatedBy(user);
            mixingPlant.setOperator(operator);
            plantResource.setMixingPlant(mixingPlant);
            em.persist(plantResource);
            try {
                return TransferObjectFactory.createTransferObject(CreateMixingPlantResourceResponse.class, plantResource);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "TOBindingError", e);
            }
            return null;
        } finally {
            em.close();
        }
    }

    public CreateConcreteTruckResourceResponse createConcreteTruckResource(CreateConcreteTruckResourceRequest cto) throws AppServiceException {
        EntityManager em = em();
        try {
            if ( null == cto.getLicencePlateNumber() ) {
                throw new NullPointerException(
                        "ConcreteTruck resource information is incomplete, you must specify a licencePlateNumber for a ConcreteTruck resource.");
            }
            if ( 0 >= cto.getRatedCapacity() ) {
                throw new IllegalStateException(
                        "ConcreteTruck resource information is incomplete, you must specify ratedCapacity for a ConcreteTruck resource.");
            }
            Query query = em.createNamedQuery("ConcreteTruck.findByLicencePlateNumber").setParameter("licencePlateNumber", cto.getLicencePlateNumber());
            try {
                Object singleResult = query.getSingleResult();
                if ( null != singleResult ) {
                    throw new IllegalStateException("licencePlateNumber already exists.  Please specify an unused licencePlateNumber.");
                }
            } catch (NoResultException expected) {
                // Ignored
            }
            ConcreteTruckResource ctr = new ConcreteTruckResource();
            ConcreteTruck ct = new ConcreteTruck();
            User user = UserSession.getUserSession().getUser();
            ct.setCreatedBy(user);
            ct.setUpdatedBy(user);
            ct.setDriverName(cto.getDriverName());
            ct.setDriverPhoneNumber(cto.getDriverPhoneNumber());
            ct.setLicencePlateNumber(cto.getLicencePlateNumber());
            ct.setRatedCapacity(cto.getRatedCapacity());
            ct.setCreatedOn(new Date());
            ctr.setConcreteTruck(ct);
            ctr.setCreatedBy(user);
            ctr.setUpdatedBy(user);
            ctr.setCreatedOn(new Date());
            em.persist(ctr);
            try {
                final CreateConcreteTruckResourceResponse result = TransferObjectFactory.createTransferObject(CreateConcreteTruckResourceResponse.class, ctr);
                return result;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "TOBindingError", e);
            }
            return null;
        } finally {
            em.close();
        }
    }

    public CreateServiceSummaryPlanResponse createServiceSummaryPlan(CreateServiceSummaryPlanRequest sspr) throws AppServiceException {
        EntityManager em = em();
        try {
            ServiceSummaryPlan summaryPlan = new ServiceSummaryPlan();
            User user = UserSession.getUserSession().getUser();
            summaryPlan.setCreatedBy(user);
            summaryPlan.setUpdatedBy(user);
            summaryPlan.setUpdatedOn(new Date());
            summaryPlan.setTotalVolume(sspr.getTotalVolume());
            PouringPartSpec spec = null;
            try {
                spec = em.find(PouringPartSpec.class, sspr.getSpecId());
            } catch (NoResultException ex) {
                throw new NullPointerException("PouringPartSpec id is invalid, you must specify a valid spec id.");
            }
            summaryPlan.setSpec(spec);
            em.persist(summaryPlan);
            try {
                return TransferObjectFactory.createTransferObject(CreateServiceSummaryPlanResponse.class, summaryPlan);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "TOBindingError", e);
            }
            return null;
        } finally {
            em.close();
        }
    }
}
