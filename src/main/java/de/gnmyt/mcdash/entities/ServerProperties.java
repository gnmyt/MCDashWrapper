package de.gnmyt.mcdash.entities;

import de.gnmyt.mcdash.api.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

public class ServerProperties {

    private final Logger LOG = new Logger(ServerProperties.class);

    Properties serverProperties = new Properties();

    /**
     * Basic constructor of the {@link ServerProperties}
     *
     * @param serverPropertiesFile The server properties file
     */
    public ServerProperties(File serverPropertiesFile) {
        try {
            serverProperties.load(FileUtils.openInputStream(serverPropertiesFile));
        } catch (Exception e) {
            LOG.error("An error occurred while loading the server properties: {}", e.getMessage());
        }
    }

    /**
     * Gets a specific property from the server properties
     *
     * @param key The key of the property
     * @return the value of the property
     */
    public String getServerProperty(String key) {
        return serverProperties.getProperty(key);
    }

    /**
     * Gets all server properties
     *
     * @return all server properties
     */
    public HashMap<String, String> getServerProperties() {
        HashMap<String, String> properties = new HashMap<>();
        serverProperties.forEach((key, value) -> properties.put((String) key, (String) value));
        return properties;
    }

    @Override
    public String toString() {
        return "ServerProperties{" + serverProperties + '}';
    }
}
