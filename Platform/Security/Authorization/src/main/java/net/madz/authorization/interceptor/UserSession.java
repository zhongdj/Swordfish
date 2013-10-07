package net.madz.authorization.interceptor;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import net.madz.authorization.entities.Tenant;
import net.madz.authorization.entities.User;

public class UserSession implements Serializable {

    private static final long serialVersionUID = 1611766096687068671L;

    private static final ThreadLocal<UserSession> userSession = new ThreadLocal<UserSession>();

    private final User user;

    private final Long tenantId;

    public static UserSession getUserSession() {
        return userSession.get();
    }

    private UserSession(User user, Long tenantId) {
        this.user = user;
        this.tenantId = tenantId;
    }

    public <T> T execute(Executeable<T> e) throws Throwable {
        try {
            doPreExecute();
            return e.doExecute();
        } finally {
            doPostExecute();
        }
    }

    private void doPreExecute() {
        userSession.set(this);
    }

    private void doPostExecute() {
        userSession.remove();
    }

    public User getUser() {
        return user;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public static class AuthorizationInterceptor implements Serializable {

        private static final long serialVersionUID = -3642130797415278358L;

        protected static final ThreadLocal<Object> firstInvokeInThread = new ThreadLocal<>();

        @Resource
        protected EJBContext context;

        @PersistenceUnit
        protected EntityManagerFactory emf;

        public AuthorizationInterceptor() {
            super();
        }

        @AroundInvoke
        public Object doIntercept(final InvocationContext invocationContext) throws Throwable {
            if ( null == firstInvokeInThread.get() ) {
                final User user = findUser(invocationContext);
                final long tenantId = user.getTenant().getId();
                final UserSession userSession = new UserSession(user, tenantId);
                firstInvokeInThread.set(new Object());
                try {
                    return userSession.execute(new Executeable<Object>() {

                        @Override
                        public Object doExecute() throws Throwable {
                            return invocationContext.proceed();
                        }
                    });
                } finally {
                    firstInvokeInThread.set(null);
                }
            } else {
                return invocationContext.proceed();
            }
        }

        protected User findUser(final InvocationContext invocationContext) {
            final String name = context.getCallerPrincipal().getName();
            final EntityManager globalEm = emf.createEntityManager();
            final Query query = globalEm.createNamedQuery("User.findByUsername").setParameter("username", name);
            final User user = (User) query.getSingleResult();
            return user;
        }
    }

    @Interceptor
    public static class SessionBeanAuthorizationInterceptor extends AuthorizationInterceptor {

        private static final long serialVersionUID = 2568044037442754520L;
    }

    @Interceptor
    public static class MessageDrivenBeanAuthorizationInterceptor extends AuthorizationInterceptor {

        private static final String TENANT_ID_KEY = "tenant.id";

        private static final long serialVersionUID = -3769295516189193086L;

        @Override
        protected User findUser(InvocationContext invocationContext) {
            Object[] parameters = invocationContext.getParameters();
            assert parameters.length == 1;
            assert parameters[0] instanceof Message;
            final Message message = (Message) parameters[0];
            try {
                assert message.propertyExists(TENANT_ID_KEY);
                long tenantId = message.getLongProperty(TENANT_ID_KEY);
                return findTenantAdmin(tenantId);
            } catch (JMSException e) {
                throw new IllegalStateException(e);
            }
        }

        private User findTenantAdmin(long tenantId) {
            final EntityManager globalEm = this.emf.createEntityManager();
            final Tenant tenant = globalEm.find(Tenant.class, tenantId);
            assert null != tenant;
            return tenant.getAdminUser();
        }
    }

    public static interface Executeable<T> {

        T doExecute() throws Throwable;
    }
}