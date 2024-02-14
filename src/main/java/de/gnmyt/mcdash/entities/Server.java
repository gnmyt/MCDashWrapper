package de.gnmyt.mcdash.entities;

import de.gnmyt.mcdash.api.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Base64;

public class Server {

    private final Logger LOG = new Logger(Server.class);

    private Thread keepAliveThread;
    private final String name;
    private ServerStatus status;
    private final ServerConfiguration configuration;
    private final ServerProperties properties;
    private final String base64Icon;
    private final int dashPort;
    private Process process;

    /**
     * Basic constructor of the server
     *
     * @param name            The name of the server
     * @param status          The status of the server
     * @param serverDirectory The directory of the server
     */
    public Server(String name, ServerStatus status, File serverDirectory) {
        this.name = name;
        this.status = status;
        this.configuration = ServerConfiguration.load(new File(serverDirectory, "mcdash.json"));
        this.properties = new ServerProperties(new File(serverDirectory, "server.properties"));

        File configFile = new File(serverDirectory, "plugins/MinecraftDashboard/config.yml");

        this.dashPort = loadPortFromConfig(configFile);
        this.base64Icon = loadIcon(serverDirectory);
    }

    /**
     * Loads the port from the config file
     * @param configFile The config file
     */
    public int loadPortFromConfig(File configFile) {
        if (configFile.exists()) {
            try {
                String content = FileUtils.readFileToString(configFile, "UTF-8");
                String[] lines = content.split("\n");
                for (String line : lines) {
                    if (line.startsWith("port:")) {
                        return Integer.parseInt(line.split(":")[1].trim());
                    }
                }
            } catch (Exception e) {
                LOG.error("An error occurred while reading the config file", e.getMessage());
            }
        }
        return 0;
    }

    /**
     * Loads the icon of the server
     * @param serverDirectory The directory of the server
     * @return the base64 icon of the server
     */
    public String loadIcon(File serverDirectory) {
        File iconFile = new File(serverDirectory, "server-icon.png");
        if (iconFile.exists()) {
            try {
                return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(iconFile));
            } catch (Exception e) {
                LOG.error("An error occurred while reading the icon file", e.getMessage());
            }
        } else {
            try {
                return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(Server.class.getResource("/server-icon.png").getFile())));
            } catch (Exception e) {
                LOG.error("An error occurred while reading the icon file", e.getMessage());
            }
        }
        return null;
    }

    /**
     * Gets the name of the server
     *
     * @return the name of the server
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the status of the server
     *
     * @return the status of the server. Represented by the {@link ServerStatus} enum
     */
    public ServerStatus getStatus() {
        return status;
    }

    /**
     * Gets the process of the server
     *
     * @return the process of the server
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Gets the configuration of the server
     *
     * @return the configuration of the server
     */
    public ServerConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the process of the server
     *
     * @param process the process of the server
     */
    public void setProcess(Process process) {
        this.process = process;

        if (process == null && keepAliveThread != null) {
            keepAliveThread.interrupt();
            keepAliveThread = null;
        }
    }

    /**
     * Sets the status of the server
     *
     * @param status the status of the server
     */
    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    /**
     * Starts the "keep alive" thread
     * This thread will check if the server is still running and will set the status to offline if the server stopped
     */
    public void checkHealth() {
        keepAliveThread = new Thread(() -> {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                LOG.error("An error occurred while waiting for the server to stop", e.getMessage());
            }
            setStatus(ServerStatus.OFFLINE);
            setProcess(null);
            LOG.info("Process of server {} stopped", name);
        });
        keepAliveThread.setName("HealthCheckThread-" + name);
        keepAliveThread.start();
    }

    /**
     * Gets the dashboard port of the server
     *
     * @return the dashboard port of the server
     */
    public int getDashPort() {
        return dashPort;
    }

    /**
     * Gets the base64 icon of the server
     * @return the base64 icon of the server
     */
    public String getBase64Icon() {
        return base64Icon;
    }

    /**
     * Gets the properties of the server
     *
     * @return the properties of the server
     */
    public ServerProperties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", configuration=" + configuration +
                ", port=" + dashPort +
                ", icon=" + base64Icon +
                ", properties=" + properties +
                '}';
    }
}
