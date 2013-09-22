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

/**
 * 
 * @author Barry
 */
@Stateless
@LocalBean
public class NewSessionBean {

    @PersistenceUnit
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
}
