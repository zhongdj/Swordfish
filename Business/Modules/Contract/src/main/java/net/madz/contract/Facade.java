package net.madz.contract;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Session Bean implementation class Facade
 */
@Stateless
@Remote
public class Facade {

    @PersistenceUnit
    EntityManagerFactory emf;

    /**
     * Default constructor.
     */
    public Facade() {
        // TODO Auto-generated constructor stub
    }

    public void sayHello() {
        final Map<String, String> properties = new HashMap<>();
        properties.put("tenant.id", "1");
        final EntityManager em = emf.createEntityManager(properties);
        em.close();
    }
}
