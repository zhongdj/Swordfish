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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.madz.authorization.entities.User;
import net.madz.common.entities.Additive;
import net.madz.common.entities.Mortar;
import net.madz.contract.entities.Address;
import net.madz.contract.entities.PouringPart;
import net.madz.contract.entities.UnitProject;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.scheduling.entities.ConcreteTruck;
import net.madz.scheduling.entities.MixingPlant;
import net.madz.scheduling.entities.PlannedSummaryTask;
import net.madz.scheduling.entities.ResourceAllocatedTask;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

/**
 * 
 * @author Barry
 */
@Stateless
@LocalBean
public class OperationBean {

    @PersistenceUnit
    private EntityManagerFactory emf;

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
        spec.setCreatedBy(user);
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
        pouringPart.setCreatedBy(user);
        pouringPart.setCreatedOn(createDate());
        pouringPart.setDeleted(false);
        pouringPart.setId(1L);
        pouringPart.setUpdatedBy(user);
        pouringPart.setUpdatedOn(createDate());
        spec.setPouringPart(pouringPart);
        UnitProject unitProject = new UnitProject();
        unitProject.setAddress(new Address());
        unitProject.setName("新龙城56栋");
        spec.setUnitProject(unitProject);
        spec.setUpdatedBy(user);
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
        stream = new StreamSource(
                "/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                        + "allocate-resources-spec-oxm.xml");
        metadataSourceMap.put("net.madz.contract.spec.entities", stream);
        stream = new StreamSource(
                "/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                        + "allocate-resources-common-oxm.xml");
        metadataSourceMap.put("net.madz.common.entities", stream);
        stream = new StreamSource(
                "/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                        + "allocate-resources-contract-oxm.xml");
        metadataSourceMap.put("net.madz.contract.entities", stream);
        stream = new StreamSource(
                "/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                        + "allocate-resources-auth-oxm.xml");
        metadataSourceMap.put("net.madz.authorization.entities", stream);
        stream = new StreamSource(
                "/Users/Barry/Professional/Workspaces/seed/Swordfish/Business/Services/Scheduling/src/main/java/net/madz/scheduling/"
                        + "allocate-resources-core-oxm.xml");
        metadataSourceMap.put("net.madz.core.entities", stream);
        final Map<String, Object> prop = new HashMap<String, Object>();
        prop.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);
        JAXBContext jc = JAXBContext.newInstance(new Class[] { PouringPartSpec.class, ResourceAllocatedTask.class },
                prop);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty("eclipselink.json.include-root", false);
        // marshaller.marshal(new
        // OperationBean().listMyPartsInConstructing().get(0), System.out);
        marshaller.marshal(new OperationBean().allocateResourceTo(1L, 1L, 1L), System.out);
    }

    public ResourceAllocatedTask allocateResourceTo(Long summaryId, Long mixingPlantId, Long concreteTruckId) {
        ResourceAllocatedTask resourceAllocatedTask = new ResourceAllocatedTask();
        resourceAllocatedTask.setSpec(createSpec());
        resourceAllocatedTask.setCreatedBy(createUser());
        resourceAllocatedTask.setMxingPlant(createMixingPlant());
        resourceAllocatedTask.setState("Created");
        resourceAllocatedTask.setCreatedOn(createDate());
        resourceAllocatedTask.setSummaryPlan(createSummaryPlan());
        resourceAllocatedTask.setTruck(createConcreteTruck());
        return resourceAllocatedTask;
    }

    private ConcreteTruck createConcreteTruck() {
        ConcreteTruck truck = new ConcreteTruck();
        truck.setCreatedBy(createUser());
        truck.setCreatedOn(createDate());
        truck.setLicencePlateNumber("黑A88888");
        truck.setRatedCapacity(18D);
        truck.setUpdatedBy(createUser());
        truck.setUpdatedOn(createDate());
        return truck;
    }

    private PlannedSummaryTask createSummaryPlan() {
        PlannedSummaryTask summaryTask = new PlannedSummaryTask();
        summaryTask.setId(1L);
        summaryTask.setCreatedBy(createUser());
        summaryTask.setCreatedOn(createDate());
        summaryTask.setPlannedVolume(500D);
        summaryTask.setSpec(createSpec());
        summaryTask.setUpdatedBy(createUser());
        summaryTask.setUpdatedOn(createDate());
        return summaryTask;
    }

    private Date createDate() {
        return new Date();
    }

    private MixingPlant createMixingPlant() {
        MixingPlant plant = new MixingPlant();
        plant.setId(1L);
        plant.setName("1号搅拌站");
        return plant;
    }
}
