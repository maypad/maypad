package de.fraunhofer.iosb.maypadbackend.repositories;

import de.fraunhofer.iosb.maypadbackend.model.Projectgroup;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectgroupRepositoryTest {

    private final int N_PROJECTGROUPS = 3;
    private List<Projectgroup> projectgroups;

    @Autowired
    private ProjectgroupRepository repository;

    @Before
    public void setup() {
        projectgroups = new ArrayList<Projectgroup>();
        for (int i = 0; i < N_PROJECTGROUPS; i++) {
            projectgroups.add(new Projectgroup());
        }
    }

    @Test
    public void testGetById() {
        for (Projectgroup g : projectgroups) {
            int id = g.getId();
            assertThat(repository.findProjectgroupById(id)).isEqualTo(g);
        }
    }

    @Test
    public void testGetAll() {
        List<Projectgroup> repoReturn = repository.findAll();
        assertThat(CollectionUtils.isEqualCollection(repoReturn, projectgroups)).isEqualTo(true);
    }

}
