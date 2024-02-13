package de.gnmyt.mcdash.entities;

import com.google.gson.Gson;
import de.gnmyt.mcdash.api.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ServerConfiguration {

    private final static Logger LOG = new Logger(ServerConfiguration.class);

    private File file;

    private String name;
    private String type;
    private String version;
    private String description;
    private int memory;
    private boolean autoStart;

    /**
     * Loads a server configuration from a file
     *
     * @param file The file you want to load the configuration from
     * @return The loaded server configuration
     */
    public static ServerConfiguration load(File file) {
        if (!file.exists()) return null;

        try {
            if (!file.getName().endsWith(".json")) return null;

            String jsonRaw = FileUtils.readFileToString(file, "UTF-8");
            ServerConfiguration configuration = new Gson().fromJson(jsonRaw, ServerConfiguration.class);
            configuration.file = file;

            return configuration;
        } catch (Exception e) {
            LOG.error("An error occurred while loading the server configuration: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Saves the server configuration to the file
     */
    public void save() {
        if (file == null) return;
        Gson gson = new Gson();
        String json = gson.toJson(this);

        try {
            FileUtils.writeStringToFile(file, json, "UTF-8");
        } catch (Exception e) {
            LOG.error("An error occurred while saving the server configuration: {}", e.getMessage());
        }
    }

    /**
     * Gets the name of the server
     *
     * @return The name of the server
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of the server
     *
     * @return The type of the server
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the version of the server
     *
     * @return The version of the server
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the description of the server
     *
     * @return The description of the server
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the memory of the server
     *
     * @return The memory of the server
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Gets the auto start of the server
     * @return The auto start of the server
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * Sets the description of the server
     *
     * @param description The new description of the server
     */
    public void setDescription(String description) {
        this.description = description;
        save();
    }

    /**
     * Sets the memory of the server
     *
     * @param memory The new memory of the server
     */
    public void setMemory(int memory) {
        this.memory = memory;
        save();
    }

    /**
     * Sets the type of the server
     *
     * @param type The new type of the server
     */
    public void setType(String type) {
        this.type = type;
        save();
    }

    /**
     * Sets the version of the server
     *
     * @param version The new version of the server
     */
    public void setVersion(String version) {
        this.version = version;
        save();
    }

    /**
     * Sets the name of the server
     *
     * @param name The new name of the server
     */
    public void setName(String name) {
        this.name = name;
        save();
    }

    /**
     * Sets the auto start of the server
     * @param autoStart The new auto start of the server
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        save();
    }
}
