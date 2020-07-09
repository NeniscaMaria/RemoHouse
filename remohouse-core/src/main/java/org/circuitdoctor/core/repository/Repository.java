package org.circuitdoctor.core.repository;

import org.circuitdoctor.core.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface Repository<T extends BaseEntity<ID>, ID extends Serializable>
        extends JpaRepository<T, ID> {
    Optional<T> findById(ID id);


}
