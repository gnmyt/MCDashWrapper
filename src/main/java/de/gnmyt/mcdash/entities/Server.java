package de.gnmyt.mcdash.entities;

import de.gnmyt.mcdash.api.Logger;

import java.io.File;

public class Server {

    private final Logger LOG = new Logger(Server.class);

    private Thread keepAliveThread;
    private final String name;
    private ServerStatus status;
    private final ServerConfiguration configuration;
    private Process process;

    /**
     * Basic constructor of the server
     *
     * @param name              The name of the server
     * @param status            The status of the server
     * @param configurationFile The configuration file of the server
     */
    public Server(String name, ServerStatus status, File configurationFile) {
        this.name = name;
        this.status = status;
        this.configuration = ServerConfiguration.load(configurationFile);
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

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", configuration=" + configuration +
                '}';
    }
}
