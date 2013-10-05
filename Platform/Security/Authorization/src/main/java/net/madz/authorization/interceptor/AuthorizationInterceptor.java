package net.madz.authorization.interceptor;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import net.madz.authorization.AuthContext;
import net.madz.authorization.entities.User;

@Authorized
@Interceptor
public class AuthorizationInterceptor implements Serializable {

    @Resource
    private EJBContext context;
    @PersistenceUnit
    private EntityManagerFactory emf;

    @AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
        System.out.println("Entering method: " + invocationContext.getMethod().getName() + " in class "
                + invocationContext.getMethod().getDeclaringClass().getName());
        final String name = context.getCallerPrincipal().getName();
        final EntityManager globalEm = emf.createEntityManager();
        final Query query = globalEm.createNamedQuery("User.findByUsername").setParameter("username",
                name);
        final User user = (User) query.getSingleResult();
        final long tenantId = user.getTenant().getId();
        AuthContext.setUser(user);
        AuthContext.setTenantId(tenantId);
        
        return invocationContext.proceed();
    }
}
