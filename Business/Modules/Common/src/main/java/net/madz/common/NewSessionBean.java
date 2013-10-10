/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.common;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import net.madz.common.entities.Concrete;
import net.madz.common.entities.ConstructionCategory;
import net.madz.common.entities.Mortar;

/**
 * 
 * @author Barry
 */
@Stateless
@LocalBean
public class NewSessionBean {

    @PersistenceUnit(name = "CRMP-PU")
    EntityManagerFactory emf;

    public void businessMethod() {
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void sayHello() {
        final Map<String, String> properties = new HashMap<>();
        properties.put("tenant.id", "1");
        final EntityManager em = emf.createEntityManager(properties);
        em.close();
    }

    public void doIt() {
        final Map<String, String> prop = new HashMap<>();
        prop.put("tenant.id", "1");
        final EntityManager em = emf.createEntityManager(prop);
        for ( Concrete.StrengthGrade grade : Concrete.StrengthGrade.values() ) {
            Concrete c = new Concrete();
            c.setGrade(grade);
            em.persist(c);
        }
        for ( Mortar.StrengthGrade grade : Mortar.StrengthGrade.values() ) {
            Mortar m = new Mortar();
            m.setGrade(grade);
            em.persist(m);
        }
        ConstructionCategory cc = new ConstructionCategory();
        cc.setName("中国");
        em.persist(cc);
    }
}
