package de.fraunhofer.iosb.maypadbackend.config.server;

import de.fraunhofer.iosb.maypadbackend.model.person.Person;
import lombok.Data;
import java.util.List;

@Data
public class BranchProperty {
    private String name;
    private String description;
    private List<Person> members;
}
