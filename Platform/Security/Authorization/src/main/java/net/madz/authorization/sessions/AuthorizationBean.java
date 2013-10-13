package net.madz.authorization.sessions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.madz.authorization.entities.Group;
import net.madz.authorization.entities.Tenant;
import net.madz.authorization.entities.User;
import net.madz.security.login.factory.EncryptorFactory;
import net.madz.utils.BusinessModuleException;

@Stateless
@Local
public class AuthorizationBean {

    @PersistenceContext
    EntityManager em;

    public void registerFreeTrial(FreeTrailTO freeTrailRequest) throws BusinessModuleException {
        final Date justNow = new Date();
        final User createdBy = new User();
        {
            createdBy.setCreatedOn(justNow);
            createdBy.setEmail(freeTrailRequest.getEmail());
            createdBy.setFullName(freeTrailRequest.getFullName());
            final List<Group> groups = new ArrayList<>();
            createdBy.setGroups(groups);
            createdBy.setLastChangePwdTime(new Timestamp(justNow.getTime()));
            String encrypted = EncryptorFactory.getInstance().getPasswordEncryptor()
                    .encrypt(freeTrailRequest.getPassword());
            createdBy.setPassword(encrypted);
            createdBy.setPhoneNumber(freeTrailRequest.getPhoneNumber());
            createdBy.setUsername(freeTrailRequest.getUserName());
            createdBy.setUpdatedOn(justNow);
            createdBy.setUpdatedBy(createdBy);
            createdBy.setCreatedBy(createdBy);
        }
        Tenant freeTrailTenant = new Tenant();
        createdBy.setTenant(freeTrailTenant);
        {
            freeTrailTenant.setFreeTrial(true);
            freeTrailTenant.setServiceDaysLeft(30);
            freeTrailTenant.setAddress(freeTrailRequest.getAddress().toString());
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(justNow);
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            Timestamp maturityDate = new Timestamp(calendar.getTimeInMillis());
            freeTrailTenant.setMaturityDate(maturityDate);
            freeTrailTenant.setName(freeTrailRequest.getCompanyName());
            freeTrailTenant.setUpdatedOn(justNow);
            freeTrailTenant.setAdminUser(createdBy);
            freeTrailTenant.setCreatedBy(createdBy);
            freeTrailTenant.setUpdatedBy(createdBy);
        }
        em.persist(freeTrailTenant);
        em.persist(createdBy);
    }
}
