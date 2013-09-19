package net.madz.authorization.sessions;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Stateless
@Remote
public class AuthBean {

    @PersistenceUnit
    EntityManagerFactory emf;

    public void sayHello() {
        final Map<String, String> properties = new HashMap<>();
        properties.put("tenant.id", "1");
        final EntityManager em = emf.createEntityManager(properties);
        em.close();
    }
}
