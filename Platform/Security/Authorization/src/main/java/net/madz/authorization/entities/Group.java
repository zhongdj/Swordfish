package net.madz.authorization.entities;

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
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

/**
 * 
 * @author Administrator
 */
@Entity
@Table(name = "mgroup")
@NamedQuery(name = "UserGroup.findGroupByName", query = "SELECT OBJECT(a) FROM Group AS a WHERE a.name=:groupName")
//@IdClass(ComposedPK.class)
public class Group extends StandardObject {

    public static final String ADMINGROUP = "ADMINGroup";
    public static final String OPGROUP = "OPGroup";
    private static final long serialVersionUID = -7787991532112147877L;
    @Column(unique = true, updatable = false, length = 40, nullable = false)
    @XmlID
    private String name;
    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    @XmlIDREF
    private List<User> users = new LinkedList<User>();
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "group_role", joinColumns = {
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "GROUP_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") }, inverseJoinColumns = {
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "ROLE_ID", insertable = true, updatable = true, referencedColumnName = "ID") })
    private List<Role> roles = new LinkedList<Role>();
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "TENANT_ID")
    private Tenant tenant;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> userList) {
        this.users = userList;
    }

    public void addUser(User userLogin) {
        this.users.add(userLogin);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        if ( role == null || role.getId() <= 0 ) {
            return;
        }
        if ( !roles.contains(role) ) {
            roles.add(role);
            role.addUserGroup(this);
        }
    }

    public void removeRole(Role role) {
        if ( role == null || role.getId() <= 0 ) {
            return;
        }
        if ( roles.contains(role) ) {
            roles.remove(role);
            role.removeUserGroup(this);
        }
    }

    public void removeAllRoles() {
        if ( roles != null && roles.size() > 0 ) {
            for ( Role role : roles ) {
                removeRole(role);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Group other = (Group) obj;
        if ( this.name == null || !this.name.equals(other.name) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + ( this.name != null ? this.name.hashCode() : 0 );
        return hash;
    }

    @Override
    public String toString() {
        String userNames = "";
        if ( users != null ) {
            for ( User abo : users ) {
                userNames += abo.getUsername() + ",";
            }
            if ( userNames.length() > 1 ) {
                userNames = userNames.substring(0, userNames.length() - 1);
            }
        }
        return getClass().getName() + ":[ name = " + name + ", accounts = " + userNames + "]";
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
