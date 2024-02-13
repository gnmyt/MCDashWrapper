package de.gnmyt.mcdash.api;

import de.gnmyt.mcdash.MCDashWrapper;

public class DashShutdownHook extends Thread {

    private static final Logger LOG = new Logger(DashShutdownHook.class);
    @Override
    public void run() {
        setName("MCDash-Shutdown-Hook");

        LOG.info("Stopping the server...");
        ServerManager serverManager = MCDashWrapper.getServerManager();
        serverManager.stopServers();
    }
}
