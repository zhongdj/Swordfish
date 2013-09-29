package net.madz.customer.sessions;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote
public class CustomerService {

    public String sayHello() {
        return "";
    }
}
