package de.gnmyt.mcdash.routes.server;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.Logger;
import de.gnmyt.mcdash.api.ServerManager;
import de.gnmyt.mcdash.api.ServerVersionManager;
import de.gnmyt.mcdash.entities.ServerConfiguration;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

import java.util.UUID;

public class SetupRoute extends DefaultHandler {

    private static final Logger LOG = new Logger(SetupRoute.class);
    private final ServerManager serverManager = MCDashWrapper.getServerManager();
    private final ServerVersionManager versionManager = MCDashWrapper.getServerManager().getVersionManager();

    @Override
    public String path() {
        return "setup";
    }

    @Override
    public void post(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "name")) return;
        if (!isStringInBody(request, response, "type")) return;
        if (!isStringInBody(request, response, "version")) return;
        if (!isStringInBody(request, response, "description")) return;
        if (!isIntegerInBody(request, response, "memory")) return;
        if (!isBooleanInBody(request, response, "autoStart")) return;

        if (!versionManager.getInstallers().containsKey(getStringFromBody(request, "type"))) {
            response.code(404).message("The server software could not be found");
            return;
        }

        try {
            String uuid = UUID.randomUUID().toString().split("-")[0];
            MCDashWrapper.getDataSource("servers/" + uuid).mkdirs();

            ServerConfiguration configuration = new ServerConfiguration(
                    getStringFromBody(request, "name"),
                    getStringFromBody(request, "type"),
                    getStringFromBody(request, "version"),
                    getStringFromBody(request, "description"),
                    getIntegerFromBody(request, "memory"),
                    getBooleanFromBody(request, "autoStart")
            );

            configuration.file = MCDashWrapper.getDataSource("servers/" + uuid + "/mcdash.json");
            configuration.save();

            versionManager.installPlugin(uuid);

            serverManager.refreshServers();

            serverManager.startServer(serverManager.getServer(uuid));

            response.json().add("uuid", uuid).add("message", "The server has been successfully created").finish();
            LOG.info("The server {} has been created", uuid);
        } catch (Exception e) {
            response.code(500).message("An error occurred while creating the server: " + e.getMessage());
            LOG.error("An error occurred while creating the server: {}", e.getMessage());
        }

    }
}
