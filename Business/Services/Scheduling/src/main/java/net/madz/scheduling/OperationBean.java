/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import net.madz.authorization.MultitenancyBean;
import net.madz.authorization.interceptor.UserSession.SessionBeanAuthorizationInterceptor;
import net.madz.binding.TransferObjectFactory;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.core.biz.BOFactory;
import net.madz.scheduling.biz.IConcreteTruckResource;
import net.madz.scheduling.biz.IMixingPlantResource;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IServiceSummaryPlan;
import net.madz.scheduling.entities.ServiceOrder;
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
            Long concreteTruckResourceId, double volume) {
        EntityManager em = em();
        try {
            final IServiceSummaryPlan summaryTask = em.find(IServiceSummaryPlan.class, summaryPlanId);
            final IServiceOrder serviceOrderBO = BOFactory.create(IServiceOrder.class);
            final IMixingPlantResource plantResource = em.find(IMixingPlantResource.class, mixingPlantResourceId);
            final IConcreteTruckResource truckResource = em.find(IConcreteTruckResource.class, concreteTruckResourceId);
            serviceOrderBO.setSummaryPlan(summaryTask);
            serviceOrderBO.allocateResources(plantResource, truckResource, volume);
            serviceOrderBO.persist(em);
            ServiceOrder serviceOrder = serviceOrderBO.get();
            try {
                return TransferObjectFactory.createTransferObject(ServiceOrderTO.class, serviceOrder);
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
}
