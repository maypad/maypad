package de.fraunhofer.iosb.maypadbackend.repositories;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Provides access to projectgroups from the datasource.
 */
@Repository
public interface ProjectgroupRepository extends JpaRepository<Projectgroup, Integer> {
    List<Projectgroup> findAll();

    Optional<Projectgroup> findProjectgroupById(Integer id);

    Optional<Projectgroup> findProjectgroupByProjectsContaining(Project project);
}
