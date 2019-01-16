/**
 * Copyright (c) 2017 Imtiaz Rahi.
 * This software may be modified and distributed under the terms of the MIT license.
 * See the LICENSE file for details.
 */

package de.fraunhofer.iosb.maypadbackend.config.factories;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Spring {@link PropertySourceFactory} implementation to read a YAML file
 * and return it as {@link PropertySource} to be bound to a POJO.
 *
 * @author Imtiaz Rahi
 * @since 2017-08-31
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name,
                                                  EncodedResource resource) throws IOException {
        if (!ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", null)) {
            return null;
        }
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setMatchDefault(true);
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();

        Properties props = factory.getObject();
        return props.isEmpty() ? null : new PropertiesPropertySource(name, props);
    }

}