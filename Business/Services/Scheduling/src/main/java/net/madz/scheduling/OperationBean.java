/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.scheduling;

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
import net.madz.scheduling.biz.IConcreteTruckResource;
import net.madz.scheduling.biz.IMixingPlantResource;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IServiceSummaryPlan;
import net.madz.scheduling.entities.ConcreteTruck;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.entities.MixingPlant;
import net.madz.scheduling.entities.MixingPlantResource;
import net.madz.scheduling.to.ConcreteTruckResourceTO;
import net.madz.scheduling.to.MixingPlantResourceTO;
import net.madz.scheduling.to.ServiceOrderTO;

/**
 * 
 * @author Barry
 */
@Stateless
@LocalBean
@Interceptors(SessionBeanAuthorizationInterceptor.class)
public class OperationBean extends MultitenancyBean {

    private static Logger logger = Logger.getLogger(OperationBean.class.getName());

    public List<PouringPartSpec> listMyPartsInConstructing() {
        final ArrayList<PouringPartSpec> result = new ArrayList<>();
        return result;
    }

    public ServiceOrderTO allocateResourceTo(Long summaryPlanId, Long mixingPlantResourceId,
            Long concreteTruckResourceId, double volume) throws AppServiceException {
        EntityManager em = em();
        try {
            final IServiceSummaryPlan summaryPlan = em.find(IServiceSummaryPlan.class, summaryPlanId);
            if ( null == summaryPlan ) {
                throw new BONotFoundException(OperationBean.class, "scheduling", "100-0001");
            }
            final IMixingPlantResource plantResource = em.find(IMixingPlantResource.class, mixingPlantResourceId);
            if ( null == plantResource ) {
                throw new BONotFoundException(OperationBean.class, "scheduling", "100-0006");
            }
            final IConcreteTruckResource truckResource = em.find(IConcreteTruckResource.class, concreteTruckResourceId);
            if ( null == truckResource ) {
                throw new BONotFoundException(OperationBean.class, "scheduling", "100-0004");
            }
            final IServiceOrder serviceOrderBO = summaryPlan.createServiceOrder(plantResource, truckResource, volume);
            serviceOrderBO.persist(em);
            try {
                return TransferObjectFactory.createTransferObject(ServiceOrderTO.class, serviceOrderBO.get());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "TOBindingError", e);
            }
            return null;
        } finally {
            em.close();
        }
    }

    public List<PouringPartSpec> filterMyPartsInConstructing(String filter) {
        return listMyPartsInConstructing();
    }

    public MixingPlantResourceTO createPlantResource(String mixingPlantName, String operatorName)
            throws AppServiceException {
        EntityManager em = em();
        try {
            if ( null == mixingPlantName || 0 >= mixingPlantName.trim().length() ) {
                throw new NullPointerException("mixing plant name is null.");
            }
            if ( null == operatorName || 0 >= operatorName.trim().length() ) {
                throw new NullPointerException("operator name is null.");
            }
            MixingPlantResource plantResource = new MixingPlantResource();
            User user = UserSession.getUserSession().getUser();
            plantResource.setCreatedBy(user);
            plantResource.setUpdatedBy(user);
            MixingPlant mixingPlant = new MixingPlant();
            mixingPlant.setCreatedBy(user);
            mixingPlant.setName(mixingPlantName);
            mixingPlant.setUpdatedBy(user);
            Query query = em.createNamedQuery("User.findByUsername").setParameter("username", operatorName);
            final User operator = (User) query.getSingleResult();
            mixingPlant.setOperator(operator);
            plantResource.setMixingPlant(mixingPlant);
            em.persist(plantResource);
            try {
                return TransferObjectFactory.createTransferObject(MixingPlantResourceTO.class, plantResource);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "TOBindingError", e);
            }
            return null;
        } finally {
            em.close();
        }
    }

    public ConcreteTruckResourceTO createConcreteTruckResource(ConcreteTruckResourceTO cto) throws AppServiceException {
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
            Query query = em.createNamedQuery("concreteTruck.findByLicencePlateNumber").setParameter(
                    "licencePlateNumber", cto.getLicencePlateNumber());
            try {
                Object singleResult = query.getSingleResult();
                if ( null != singleResult ) {
                    throw new IllegalStateException(
                            "licencePlateNumber already exists.  Please specify an unused licencePlateNumber.");
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
                final ConcreteTruckResourceTO result = TransferObjectFactory.createTransferObject(
                        ConcreteTruckResourceTO.class, ctr);
                return result;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "TOBindingError", e);
            }
            return null;
        } finally {
            em.close();
        }
    }
}
