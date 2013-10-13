package net.madz.authorization.entities;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;

/**
 * 
 * @author Barry Zhong
 */
@Entity
@Table(name = "muser")
@NamedQueries({
        @NamedQuery(name = "User.validate",
                query = "SELECT COUNT(a) FROM User AS a WHERE a.username = :accountName AND a.password= :password"),
        @NamedQuery(name = "User.checkExist", query = "SELECT COUNT(a) FROM User AS a WHERE a.username = :accountName"),
        @NamedQuery(name = "User.checkNotLocked",
                query = "SELECT COUNT(a) FROM User AS a WHERE a.lockFlag = false AND a.username=:accountName"),
        @NamedQuery(name = "User.findByUsername",
                query = "SELECT OBJECT(a) FROM User AS a WHERE a.username = :username"),
        @NamedQuery(name = "User.findByAccountNameFuzzy",
                query = "SELECT OBJECT(a) FROM User AS a WHERE a.username LIKE :accountName"),
        @NamedQuery(name = "User.findByAccountIdFuzzy", query = "SELECT OBJECT(a) FROM User AS a WHERE a.id LIKE :id") })
@Index(name = "INDEX_USER_TENANT_FIRST", columnNames = { "TENANT_ID" })
public class User extends StandardObject {

    private static final long serialVersionUID = 1L;

    @Column(unique = true, updatable = false, length = 40, nullable = false)
    @XmlID
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column
    private String phoneNumber;

    @Column(columnDefinition = "BOOL NOT NULL DEFAULT 0")
    private boolean lockFlag;

    @Column(columnDefinition = "INT(32) NOT NULL DEFAULT 0")
    private int loginTimes;

    @Column(columnDefinition = "INT(4) NOT NULL DEFAULT 0")
    private int loginFailedTimes;

    @Column(columnDefinition = "INT(4) NOT NULL DEFAULT 0")
    private int accessDeniedTimes;

    @Column(columnDefinition = "BOOL NOT NULL DEFAULT 0")
    private boolean freezenFlag;

    @Column(columnDefinition = "BOOL NOT NULL DEFAULT 0")
    private boolean needResetPwd;

    @Column(nullable = true)
    private Timestamp loginDate;

    @Column(nullable = true)
    private Timestamp lastLoginDate;

    @Column(nullable = true)
    private Timestamp lastFailedTime;

    @Column(nullable = true)
    private Timestamp lastChangePwdTime;

    @Column(nullable = true)
    private String oldPasswords;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "user_group", joinColumns = {
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = true, updatable = true,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME") }, inverseJoinColumns = {
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "GROUP_NAME", referencedColumnName = "NAME") })
    @XmlIDREF
    private final List<Group> groups = new LinkedList<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = true, updatable = true,
                    referencedColumnName = "TENANT_ID"), @JoinColumn(name = "USER_ID", referencedColumnName = "ID") },
            inverseJoinColumns = {
                    @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                            referencedColumnName = "TENANT_ID"),
                    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") })
    @XmlIDREF
    private final List<Role> roles = new LinkedList<>();

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TENANT_ID", insertable = true, updatable = false, nullable = false, referencedColumnName = "ID")
    private Tenant aTenant;

    public User() {
    }

    public User(String accountName, String password, List<Group> groups, Tenant company) {
        this.setUsername(accountName);
        this.setPassword(password);
        this.setNeedResetPwd(true);
        this.setGroups(groups);
    }

    public int getAccessDeniedTimes() {
        return accessDeniedTimes;
    }

    public void setAccessDeniedTimes(int accessDeniedTimes) {
        this.accessDeniedTimes = accessDeniedTimes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String accountName) {
        this.username = accountName;
    }

    public boolean isFreezenFlag() {
        return freezenFlag;
    }

    public void setFreezenFlag(boolean freezenFlag) {
        this.freezenFlag = freezenFlag;
    }

    public Timestamp getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Timestamp loginDate) {
        this.loginDate = loginDate;
    }

    public Timestamp getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Timestamp lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public boolean isLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public int getLoginFailedTimes() {
        return loginFailedTimes;
    }

    public void setLoginFailedTimes(int loginFailedTimes) {
        this.loginFailedTimes = loginFailedTimes;
    }

    public int getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(int loginTimes) {
        this.loginTimes = loginTimes;
    }

    public boolean isNeedResetPwd() {
        return needResetPwd;
    }

    public void setNeedResetPwd(boolean needResetPwd) {
        this.needResetPwd = needResetPwd;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getLastFailedTime() {
        return lastFailedTime;
    }

    public void setLastFailedTime(Timestamp lastFailedTime) {
        this.lastFailedTime = lastFailedTime;
    }

    public Timestamp getLastChangePwdTime() {
        return lastChangePwdTime;
    }

    public void setLastChangePwdTime(Timestamp lastChangePwdTime) {
        this.lastChangePwdTime = lastChangePwdTime;
    }

    public String getOldPasswords() {
        return oldPasswords;
    }

    public void setOldPasswords(String oldPasswords) {
        this.oldPasswords = oldPasswords;
    }

    @XmlTransient
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        if ( this.groups != null && this.groups.size() > 0 ) {
            for ( Group userGroup : this.groups ) {
                userGroup.removeUser(this);
            }
        }
        this.groups.clear();
        this.groups.addAll(groups);
        for ( Group userGroup : groups ) {
            userGroup.addUser(this);
        }
    }

    public void removeAllUserGroup() {
        if ( getGroups() != null && getGroups().size() > 0 ) {
            for ( Group group : getGroups() ) {
                removeUserGroup(group);
            }
        }
    }

    public Tenant getTenant() {
        return aTenant;
    }

    public void addUserGroup(Group group) {
        if ( !groups.contains(group) ) {
            groups.add(group);
            group.addUser(this);
        }
    }

    public void removeUserGroup(Group group) {
        if ( groups.contains(group) ) {
            groups.remove(group);
            group.removeUser(this);
        }
    }

    @XmlTransient
    public List<Role> getRoles() {
        return roles;
    }

    public final void setRoles(List<Role> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public void addRole(Role role) {
        if ( null != role && role.getId() > 0 ) {
            if ( !roles.contains(role) ) {
                roles.add(role);
                role.addUser(this);
            }
        }
    }

    public void removeRole(Role role) {
        if ( role == null || role.getId() <= 0 ) {
            return;
        }
        if ( roles.contains(role) ) {
            roles.remove(role);
            role.removeUser(this);
        }
    }

    public void removeAllRoles() {
        if ( getRoles() != null && getRoles().size() > 0 ) {
            for ( Role role : getRoles() ) {
                removeRole(role);
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Business Methods
    public void freeze() {
        this.setFreezenFlag(true);
    }

    public void unFreezeAccount() {
        this.setFreezenFlag(false);
    }

    public void lock() {
        this.setLockFlag(true);
    }

    public void unlock() {
        this.setLockFlag(false);
    }

    public void resetPassword(String oldPassword, String newPassword, String confirmPassword) {
        // TODO implements resetPassword
    }

    @Override
    public String toString() {
        String groupStr = "";
        if ( groups != null ) {
            for ( Group group : groups ) {
                groupStr += group.getName();
            }
        }
        StringBuilder s = new StringBuilder();
        s.append("Account [ name=").append(username).append(", groups=").append(groupStr).append(", lockFlag=")
                .append(lockFlag).append(", loginTimes=").append(loginTimes).append(", loginFailedTimes=")
                .append(loginFailedTimes).append(", accessDeniedTimes=").append(accessDeniedTimes).append(", freezen=")
                .append(freezenFlag).append(", loginDate =").append(loginDate).append(", lastChangePwdTime=")
                .append(lastChangePwdTime).append("]");
        return s.toString();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTenant(Tenant tenant) {
        this.aTenant = tenant;
    }
}
