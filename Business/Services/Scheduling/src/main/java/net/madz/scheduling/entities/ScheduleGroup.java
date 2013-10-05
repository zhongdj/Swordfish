package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.authorization.entities.User;
import net.madz.contract.entities.UnitProject;

@Entity
@Table(name="scheduling_group")
public class ScheduleGroup extends MultiTenancyEntity {

    private static final long serialVersionUID = -4802888767536025513L;

    @Column(nullable = false, length = 20)
    private String name;

    @OneToMany
    @JoinTable(name = "scheduling_user_group",
            joinColumns = {
                    @JoinColumn(name = "TENANT_ID", insertable = true, updatable = false,
                            referencedColumnName = "TENANT_ID"),
                    @JoinColumn(name = "SCHEDULE_GROUP_ID", insertable = true, updatable = false,
                            referencedColumnName = "ID") }, inverseJoinColumns = {
                    @JoinColumn(name = "TENANT_ID", insertable = false, updatable = false,
                            referencedColumnName = "TENANT_ID"),
                    @JoinColumn(name = "SCHEDULOR_ID", insertable = true, updatable = false,
                            referencedColumnName = "ID") })
    private final List<User> schedulors = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "scheduling_group_unit_project",
            joinColumns = {
                    @JoinColumn(name = "TENANT_ID", insertable = true, updatable = false,
                            referencedColumnName = "TENANT_ID"),
                    @JoinColumn(name = "SCHEDULE_GROUP_ID", insertable = true, updatable = false,
                            referencedColumnName = "ID") }, inverseJoinColumns = {
                    @JoinColumn(name = "TENANT_ID", insertable = false, updatable = false,
                            referencedColumnName = "TENANT_ID"),
                    @JoinColumn(name = "UNIT_PROJECT_ID", insertable = true, updatable = false,
                            referencedColumnName = "ID") })
    private final List<UnitProject> unitProjects = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getSchedulors() {
        return schedulors;
    }

    public List<UnitProject> getUnitProjects() {
        return unitProjects;
    }

    public void addSchedulor(User user) {
        this.schedulors.add(user);
    }

    public void addUnitProject(UnitProject unitProject) {
        this.unitProjects.add(unitProject);
    }

    public void setSchedulors(List<User> schedulors) {
        this.schedulors.clear();
        this.schedulors.addAll(schedulors);
    }
}
