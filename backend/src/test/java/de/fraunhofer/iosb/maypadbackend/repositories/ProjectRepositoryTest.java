package de.fraunhofer.iosb.maypadbackend.repositories;

import de.fraunhofer.iosb.maypadbackend.model.Project;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ProjectRepositoryTest {

    private final int N_PROJECTS = 3;
    private List<Project> projects;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProjectRepository repository;

    @Before
    public void setup() {
        projects = new ArrayList<Project>();
        for (int i = 0; i < N_PROJECTS; i++) {
            projects.add(new Project());
            em.persist(projects.get(i));
        }
    }

    @Test
    public void testGetById() {
        for (Project p : projects) {
            assertThat(repository.findProjectById(p.getId()).get()).isEqualTo(p);
        }
    }

    @Test
    public void testGetAll() {
        assertThat(CollectionUtils.isEqualCollection(projects, repository.findAll())).isEqualTo(true);
    }

}
