package net.madz.rs.contract;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

import net.madz.rs.contract.resources.ContractResources;
import net.madz.rs.scheduling.providers.MadzExceptionMapper;

@ApplicationPath("/contracting")
public class ContractApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final HashSet<Class<?>> resources = new HashSet<>();
        resources.add(MOXyJsonProvider.class);
        resources.add(MadzExceptionMapper.class);
        resources.add(ContractResources.class);
        return resources;
    }
}
