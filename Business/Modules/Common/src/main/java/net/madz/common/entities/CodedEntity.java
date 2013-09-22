package net.madz.common.entities;

import javax.persistence.MappedSuperclass;

import net.madz.core.entities.AbstractBaseEntity;

@MappedSuperclass
public abstract class CodedEntity extends AbstractBaseEntity {

    protected String code;

    public CodedEntity() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}