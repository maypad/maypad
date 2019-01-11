package de.fraunhofer.iosb.maypadbackend.repositories;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Provides access to projects from the datasource.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    public List<Project> findAll();

    public Optional<Project> findProjectById(Integer id);
}
