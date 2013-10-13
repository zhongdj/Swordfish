package net.madz.rs.registration;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import net.madz.rs.registration.resources.RegistrationResources;
import net.madz.rs.scheduling.providers.MadzExceptionMapper;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

@Stateless
@ApplicationPath("/auth")
public class AuthorizationApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final HashSet<Class<?>> resources = new HashSet<>();
        resources.add(MOXyJsonProvider.class);
        resources.add(MadzExceptionMapper.class);
        resources.add(RegistrationResources.class);
        return resources;
    }
}
