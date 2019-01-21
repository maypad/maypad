package de.fraunhofer.iosb.maypadbackend.repositories;

import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class ProjectgroupRepositoryImpl extends SimpleJpaRepository<Projectgroup, Integer> implements ProjectgroupRepository {

    private EntityManager entityManager;

    public ProjectgroupRepositoryImpl(EntityManager entityManager) {
        super(Projectgroup.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Projectgroup> findProjectgroupById(Integer id) {
        return Optional.ofNullable(this.getOne(id));
    }
}
