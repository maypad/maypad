package de.fraunhofer.iosb.maypadbackend.config.project;

import de.fraunhofer.iosb.maypadbackend.config.project.data.BranchProperty;
import de.fraunhofer.iosb.maypadbackend.config.project.data.ProjectConfigData;
import de.fraunhofer.iosb.maypadbackend.exceptions.FormatParseException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class YamlProjectConfig implements ProjectConfig {

    private ProjectConfigData yamlObject;

    /**
     * Constructs a new ServerConfigImpl with a given YAML-File.
     *
     * @param yamlFile The YAML-File to be parsed.
     */
    public YamlProjectConfig(File yamlFile) throws IOException {
        Yaml yaml = new Yaml(new Constructor(ProjectConfigData.class));
        try {
            FileInputStream in = new FileInputStream(yamlFile);
            this.yamlObject = yaml.load(in);
            in.close();
        } catch (IOException e) {
            throw new FileNotFoundException("File " + yamlFile.getAbsolutePath() + " does not exist!");
        } catch (YAMLException e) {
            throw new FormatParseException("File " + yamlFile.getAbsolutePath() + " could not be parsed!");
        }
    }


    /**
     * Returns project name configured in Maypad.
     *
     * @return The project name.
     */
    @Override
    public String getProjectName() {
        return yamlObject.getProjectName();
    }

    /**
     * Returns project description configured in Maypad.
     *
     * @return The project description.
     */
    @Override
    public String getProjectDescription() {
        return yamlObject.getProjectDescription();
    }

    /**
     * Returns property, whether all branches should be added to project or not.
     *
     * @return true if all branches of repository should be added to project, false otherwise.
     */
    @Override
    public boolean getAddAllBranches() {
        return yamlObject.getAllBranches();
    }

    /**
     * List of all Branches specified in property file.
     * Conversion to Branch-Entity happens in service-level.
     *
     * @return List of BranchProperty-Objects describing branches.
     */
    @Override
    public List<BranchProperty> getBranchProperties() {
        return yamlObject.getBranches();
    }
}
