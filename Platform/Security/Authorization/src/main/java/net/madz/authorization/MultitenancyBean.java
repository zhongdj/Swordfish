package net.madz.authorization;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import net.madz.authorization.entities.User;
import net.madz.core.biz.BizObjectManager;

public class MultitenancyBean {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Resource
    private EJBContext context;

    public MultitenancyBean() {
        super();
    }

    protected EntityManager em() {
        Map<String, Long> tenantIdKey = new HashMap<>();
        EntityManager globalEm = emf.createEntityManager();
        try {
            Query query = globalEm.createNamedQuery("User.findByUsername").setParameter("username",
                    context.getCallerPrincipal().getName());
            User user = (User) query.getSingleResult();
            long tenantId = user.getTenant().getId();
            tenantIdKey.put("tenant.id", tenantId);
            EntityManager em = emf.createEntityManager(tenantIdKey);
            return new BizObjectManager(em);
        } finally {
            globalEm.close();
        }
    }
}