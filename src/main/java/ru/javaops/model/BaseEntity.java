package ru.javaops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.hateoas.Identifiable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

/**
 * User: gkislin
 * Date: 22.08.2014
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public class BaseEntity extends AbstractPersistable<Integer> implements Identifiable<Integer> {

    public BaseEntity() {
    }

    protected BaseEntity(Integer id) {
        setId(id);
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return super.isNew();
    }
}
