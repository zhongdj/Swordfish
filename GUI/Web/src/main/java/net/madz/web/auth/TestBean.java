package net.madz.web.auth;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import net.madz.common.NewSessionBean;

@Named
@RequestScoped
public class TestBean {

    @EJB
    private NewSessionBean newSessionBean;

    public void doIt() {
        newSessionBean.doIt();
        System.out.println();
    }
}
