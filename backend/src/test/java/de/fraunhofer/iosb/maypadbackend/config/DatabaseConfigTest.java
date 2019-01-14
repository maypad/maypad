package de.fraunhofer.iosb.maypadbackend.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseConfigTest {

    @Autowired
    private DatabaseConfig dbconfig;

    @Test
    public void testLoadedValues() {
        assert(dbconfig.getUser().equals("test_user"));
        assert(dbconfig.getPassword().equals("test_password"));
        assert(dbconfig.getDatabase().equals("test_database"));
        assert(dbconfig.getHost().equals("127.0.0.1"));
        assert(dbconfig.getPort() == 7357);
    }

}
