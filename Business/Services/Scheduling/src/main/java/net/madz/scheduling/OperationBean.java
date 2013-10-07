/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.madz.authorization.MultitenancyBean;
import net.madz.authorization.entities.User;
import net.madz.authorization.interceptor.Authorized;
import net.madz.authorization.interceptor.UserSession.SessionBeanAuthorizationInterceptor;
import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.common.entities.Mortar;
import net.madz.contract.entities.PouringPart;
import net.madz.contract.entities.UnitProject;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.core.biz.BOFactory;
import net.madz.scheduling.biz.IConcreteTruckResource;
import net.madz.scheduling.biz.IMixingPlantResource;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IServiceSummaryPlan;
import net.madz.scheduling.entities.ConcreteTruck;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.entities.MixingPlant;
import net.madz.scheduling.entities.MixingPlantResource;
import net.madz.scheduling.entities.ServiceOrder;
import net.madz.scheduling.entities.ServiceSummaryPlan;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

/**
 * 
 * @author Barry
 */
@Authorized
@Stateless
@LocalBean
@Interceptors(SessionBeanAuthorizationInterceptor.class)
public class OperationBean extends MultitenancyBean {

    public List<PouringPartSpec> listMyPartsInConstructing() {
        final ArrayList<PouringPartSpec> result = new ArrayList<>();
        PouringPartSpec spec = createSpec();
        result.add(spec);
        return result;
    }

    private PouringPartSpec createSpec() {
        PouringPartSpec spec = new PouringPartSpec();
        final List<Additive> additives = new ArrayList<Additive>();
        Additive additive = new Additive();
        additive.setName("抗渗S10");
        additives.add(additive);
        additive = new Additive();
        additive.setName("早强");
        additives.add(additive);
        spec.setAdditives(additives);
        User user = createUser();
//         spec.setCreatedBy(user);
        spec.setId(1L);
        spec.setCreatedOn(createDate());
        spec.setDeleted(false);
        // Concrete mixture = new Concrete();
        // mixture.setGrade(Concrete.StrengthGrade.C30);
        Mortar mixture = new Mortar();
        mixture.setGrade(Mortar.StrengthGrade.M10);
        mixture.setId(1L);
        spec.setMixture(mixture);
        final PouringPart pouringPart = new PouringPart();
        pouringPart.setName("底柱");
        // pouringPart.setCreatedBy(user);
        pouringPart.setCreatedOn(createDate());
        pouringPart.setDeleted(false);
        pouringPart.setId(1L);
        // pouringPart.setUpdatedBy(user);
        pouringPart.setUpdatedOn(createDate());
        spec.setPouringPart(pouringPart);
        UnitProject unitProject = new UnitProject();
        unitProject.setAddress(new Address());
        unitProject.setName("新龙城56栋");
        spec.setUnitProject(unitProject);
        // spec.setUpdatedBy(user);
        spec.setUpdatedOn(createDate());
        return spec;
    }

    private User createUser() {
        User user = new User();
        user.setUsername("tracy@126.com");
        return user;
    }

    public static void main(String[] args) throws JAXBException {
        final Map<String, Source> metadataSourceMap = new HashMap<String, Source>();
        StreamSource stream = new StreamSource(
                "/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                        + "allocate-resources-oxm.xml");
        metadataSourceMap.put("net.madz.scheduling.entities", stream);
        stream = new StreamSource("/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                + "allocate-resources-spec-oxm.xml");
        metadataSourceMap.put("net.madz.contract.spec.entities", stream);
        stream = new StreamSource("/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                + "allocate-resources-common-oxm.xml");
        metadataSourceMap.put("net.madz.common.entities", stream);
        stream = new StreamSource("/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                + "allocate-resources-contract-oxm.xml");
        metadataSourceMap.put("net.madz.contract.entities", stream);
        stream = new StreamSource("/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                + "allocate-resources-auth-oxm.xml");
        metadataSourceMap.put("net.madz.authorization.entities", stream);
        stream = new StreamSource("/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                + "allocate-resources-core-oxm.xml");
        metadataSourceMap.put("net.madz.core.entities", stream);
        final Map<String, Object> prop = new HashMap<String, Object>();
        prop.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);
        JAXBContext jc = JAXBContext.newInstance(new Class[] { PouringPartSpec.class, ServiceOrder.class }, prop);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty("eclipselink.json.include-root", false);
        // marshaller.marshal(new
        // OperationBean().listMyPartsInConstructing().get(0), System.out);
        marshaller.marshal(new OperationBean().allocateResourceTo(1L, 1L, 1L, 6.0D), System.out);
    }

    public ServiceOrder allocateResourceTo(Long summaryId, Long mixingPlantResourceId, Long concreteTruckResourceId, double volume) {
        EntityManager em = em();
        try {
            final IServiceSummaryPlan summaryTask = em.find(IServiceSummaryPlan.class, summaryId);
            final IServiceOrder serviceOrder = BOFactory.create(IServiceOrder.class);
            final IMixingPlantResource plantResource = em.find(IMixingPlantResource.class, mixingPlantResourceId);
            final IConcreteTruckResource truckResource = em.find(IConcreteTruckResource.class, concreteTruckResourceId);
            serviceOrder.setSummaryPlan(summaryTask);
            serviceOrder.allocateResources(plantResource, truckResource, volume);
            
            serviceOrder.persist(em);
            
            return serviceOrder.get();
        } finally {
            em.close();
        }
    }

    private ConcreteTruckResource createConcreteTruck() {
        ConcreteTruck truck = new ConcreteTruck();
        // truck.setCreatedBy(createUser());
        truck.setCreatedOn(createDate());
        truck.setLicencePlateNumber("黑A88888");
        truck.setRatedCapacity(18D);
        // truck.setUpdatedBy(createUser());
        truck.setUpdatedOn(createDate());
        ConcreteTruckResource resource = new ConcreteTruckResource();
        resource.setConcreteTruck(truck);
        // resource.setState("Available");
        return resource;
    }

    private ServiceSummaryPlan createSummaryPlan() {
        ServiceSummaryPlan summaryTask = new ServiceSummaryPlan();
        summaryTask.setId(1L);
        // summaryTask.setCreatedBy(createUser());
        summaryTask.setCreatedOn(createDate());
        summaryTask.setPlannedVolume(500D);
        summaryTask.setSpec(createSpec());
        // summaryTask.setUpdatedBy(createUser());
        summaryTask.setUpdatedOn(createDate());
        return summaryTask;
    }

    private Date createDate() {
        return new Date();
    }

    private MixingPlantResource createMixingPlant() {
        MixingPlant plant = new MixingPlant();
        plant.setId(1L);
        plant.setName("1号搅拌站");
        MixingPlantResource r = new MixingPlantResource();
        r.setMixingPlant(plant);
        return r;
    }

    public List<PouringPartSpec> filterMyPartsInConstructing(String filter) {
        return listMyPartsInConstructing();
    }

    public IServiceOrder createOrder() {
        return null;
    }
}
