/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

import net.madz.authorization.entities.User;
import net.madz.common.entities.Additive;
import net.madz.common.entities.Concrete;
import net.madz.contract.entities.Address;
import net.madz.contract.entities.PouringPart;
import net.madz.contract.entities.UnitProject;
import net.madz.contract.spec.entities.PouringPartSpec;

/**
 * 
 * @author Barry
 */
@Stateless
@LocalBean
public class OperationBean {

    public List<PouringPartSpec> listMyPartsInConstructing() {
        final ArrayList<PouringPartSpec> result = new ArrayList<>();
        PouringPartSpec spec = new PouringPartSpec();
        final List<Additive> additives = new ArrayList<Additive>();
        Additive additive = new Additive();
        additive.setName("抗渗S10");
        additives.add(additive);
        additive = new Additive();
        additive.setName("早强");
        additives.add(additive);
        spec.setAdditives(additives);
        User user = new User();
        user.setUsername("tracy@126.com");
        spec.setCreatedBy(user);
        spec.setId(1L);
        spec.setCreatedOn(new Date());
        spec.setDeleted(false);
        Concrete mixture = new Concrete();
        mixture.setGrade(Concrete.StrengthGrade.C30);
        mixture.setId(1L);
        spec.setMixture(mixture);
        final PouringPart pouringPart = new PouringPart();
        pouringPart.setName("底柱");
        pouringPart.setCreatedBy(user);
        pouringPart.setCreatedOn(new Date());
        pouringPart.setDeleted(false);
        pouringPart.setId(1L);
        pouringPart.setUpdatedBy(user);
        pouringPart.setUpdatedOn(new Date());
        spec.setPouringPart(pouringPart);
        UnitProject unitProject = new UnitProject();
        unitProject.setAddress(new Address());
        unitProject.setName("新龙城56栋");
        spec.setUnitProject(unitProject);
        spec.setUpdatedBy(user);
        spec.setUpdatedOn(new Date());
        result.add(spec);
        return result;
    }

    public static void main(String[] args) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(PouringPartSpec.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
        //marshaller.setProperty("eclipselink.json.include-root", false);
        marshaller.marshal(new OperationBean().listMyPartsInConstructing().get(0), System.out);
    }
}
