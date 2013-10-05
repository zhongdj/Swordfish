package net.madz.authorization;

import net.madz.authorization.entities.User;

public class AuthContext {

    private static ThreadLocal<Long> tenantId = new ThreadLocal<Long>();
    private static ThreadLocal<User> user = new ThreadLocal<User>();

    private AuthContext() {
        
    }
    public static Long getTenantId() {
        return tenantId.get();
    }

    public static void setTenantId(Long tenantId) {
        AuthContext.tenantId.set(tenantId);
    }

    public static User getUser() {
        return user.get();
    }

    public static void setUser(User user) {
        AuthContext.user.set(user);
    }
}
