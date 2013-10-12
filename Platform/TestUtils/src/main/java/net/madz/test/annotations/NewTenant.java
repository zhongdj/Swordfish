package net.madz.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;

import net.madz.authorization.entities.User;
import net.madz.authorization.interceptor.UserSession;
import net.madz.authorization.interceptor.UserSession.Executeable;
import net.madz.test.annotations.NewTenant.NewTenantProcessor;
import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.Processor;

@Processor(NewTenantProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface NewTenant {

    public static class NewTenantProcessor extends AbsScriptEngine<NewTenant> {

        @Override
        public void doProcess(final TestContext context, NewTenant t) {
            increaseIndent();
            debug("Creating New Tenant and Inject UserSession");
            Constructor<UserSession> constructor = null;
            try {
                constructor = UserSession.class.getDeclaredConstructor(User.class, Long.class);
                constructor.setAccessible(true);
                Long tenantId = 1L;
                User user = new User();
                UserSession session = constructor.newInstance(user, tenantId);
                session.execute(new Executeable<Void>() {

                    @Override
                    public Void doExecute() throws Throwable {
                        context.getBase().evaluate();
                        return null;
                    }
                });
            } catch (Throwable e) {
                throw new RuntimeException(e);
            } finally {
                if ( null != constructor ) {
                    constructor.setAccessible(false);
                }
                debug("UserSession had been removed.");
                decreaseIndent();
            }
        }
    }
}
