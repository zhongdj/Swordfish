package net.madz.authorization.entities;

import java.io.Serializable;

public class ComposedPK implements Serializable {

    private static final long serialVersionUID = -2751724340081615108L;
    protected long tenant;
    protected long id;

    public ComposedPK(long tenant, long id) {
        super();
        this.tenant = tenant;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTenant() {
        return tenant;
    }

    public void setTenant(long tenant) {
        this.tenant = tenant;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( tenant ^ ( tenant >>> 32 ) );
        result = prime * result + (int) ( id ^ ( id >>> 32 ) );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ComposedPK other = (ComposedPK) obj;
        if ( tenant != other.tenant ) return false;
        if ( id != other.id ) return false;
        return true;
    }
}
