package de.gnmyt.mcdash.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import de.gnmyt.mcdash.api.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;

public class ServerConfiguration {

    private final static Logger LOG = new Logger(ServerConfiguration.class);

    @Expose(serialize = false, deserialize = false)
    public File file;

    @Expose
    private String name;

    @Expose
    private String type;

    @Expose
    private String version;

    @Expose
    private String description;

    @Expose
    private int memory;

    @Expose(serialize = true, deserialize = true)
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

            ServerConfiguration configuration =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    .fromJson(jsonRaw, ServerConfiguration.class);
            configuration.file = file;

            return configuration;
        } catch (Exception e) {
            LOG.error("An error occurred while loading the server configuration: {}", e.getMessage());
            return null;
        }
    }

    public ServerConfiguration(String name, String type, String version, String description, int memory, boolean autoStart) {
        this.name = name;
        this.type = type;
        this.version = version;
        this.description = description;
        this.memory = memory;
        this.autoStart = autoStart;
    }

    public ServerConfiguration() {

    }

    /**
     * Saves the server configuration to the file
     */
    public void save() {
        if (file == null) return;
        String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);

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

    /**
     * Creates a new server configuration
     * @return The created server configuration
     */
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("type", type);
        map.put("version", version);
        map.put("description", description);
        map.put("memory", memory);
        map.put("autoStart", autoStart);
        return map;
    }

    @Override
    public String toString() {
        return "ServerConfiguration{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", memory=" + memory +
                ", autoStart=" + autoStart +
                '}';
    }
}
