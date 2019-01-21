package de.fraunhofer.iosb.maypadbackend.repositories;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class ProjectRepositoryImpl extends SimpleJpaRepository<Project, Integer> implements ProjectRepository {

    private EntityManager entityManager;

    public ProjectRepositoryImpl(EntityManager entityManager) {
        super(Project.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Project> findProjectById(Integer id) {
        Project ret = this.getOne(id);
        return Optional.ofNullable(ret);
    }
}
