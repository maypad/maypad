package de.fraunhofer.iosb.maypadbackend.config.project.data;

import lombok.Data;

/**
 * Class to store header properties (e.g. HTTP-headers for a webhook) as listed in Maypad YAML-File.
 */
@Data
public class HeaderProperty {
    String key;
    String[] values;
}
