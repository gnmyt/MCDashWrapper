package de.gnmyt.mcdash.api;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class ConfigurationManager {

    private static final Logger LOG = new Logger(ConfigurationManager.class);
    private String version = null;

    /**
     * Gets the version of the MCDash wrapper
     * @return the version of the MCDash wrapper
     */
    public String getVersion() {
        if (version != null) {
            return version;
        }

        try {
            Enumeration<URL> resources = getClass().getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");

            while (resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                version = manifest.getMainAttributes().getValue("Version");
                if (version != null) {
                    return version;
                }
            }

        } catch (IOException e) {
            LOG.warn("Could not read version from manifest file: {}", e.getMessage());
        }

        return "DEVELOPMENT";
    }

}
