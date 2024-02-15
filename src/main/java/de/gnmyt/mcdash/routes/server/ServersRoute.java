package de.gnmyt.mcdash.routes.server;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.ServerManager;
import de.gnmyt.mcdash.api.ServerVersionManager;
import de.gnmyt.mcdash.entities.Server;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ServersRoute extends DefaultHandler {

    private final ServerManager serverManager = MCDashWrapper.getServerManager();
    private final ServerVersionManager versionManager = MCDashWrapper.getServerManager().getVersionManager();

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        ArrayList<HashMap<String, Object>> servers = new ArrayList<>();

        for (Server server : serverManager.getServers()) {
            HashMap<String, Object> serverObj = new HashMap<>();

            serverObj.put("uuid", server.getName());
            serverObj.put("status", server.getStatus().name());

            serverObj.put("motd", server.getProperties().getServerProperty("motd"));
            serverObj.put("port", server.getProperties().getServerProperty("server-port"));
            serverObj.put("icon", server.getBase64Icon());

            serverObj.put("configuration", server.getConfiguration().toHashMap());

            servers.add(serverObj);
        }

        response.jsonArray(servers);
    }

    @Override
    public void patch(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "uuid")) return;

        if (serverManager.getServer(getStringFromBody(request, "uuid")) == null) {
            response.code(404).message("The server does not exist");
            return;
        }

        Server server = serverManager.getServer(getStringFromBody(request, "uuid"));

        int totalChanges = 0;

        if (request.getBody().containsKey("name") && isStringInBody(request, response, "name")) {
            server.getConfiguration().setName(getStringFromBody(request, "name"));
            totalChanges++;
        }

        if (request.getBody().containsKey("description") && isStringInBody(request, response, "description")) {
            server.getConfiguration().setDescription(getStringFromBody(request, "description"));
            totalChanges++;
        }

        if (request.getBody().containsKey("type") && isStringInBody(request, response, "type")) {
            if (!versionManager.getInstallers().containsKey(getStringFromBody(request, "type"))) {
                response.code(404).message("The server software could not be found");
                return;
            }

            server.getConfiguration().setType(getStringFromBody(request, "type"));
            totalChanges++;
        }

        if (request.getBody().containsKey("version") && isStringInBody(request, response, "version")) {
            server.getConfiguration().setVersion(getStringFromBody(request, "version"));
            totalChanges++;
        }

        if (request.getBody().containsKey("memory") && isIntegerInBody(request, response, "memory")) {
            server.getConfiguration().setMemory(getIntegerFromBody(request, "memory"));
            totalChanges++;
        }

        if (request.getBody().containsKey("autoStart") && isBooleanInBody(request, response, "autoStart")) {
            server.getConfiguration().setAutoStart(getBooleanFromBody(request, "autoStart"));
            totalChanges++;
        }

        if (totalChanges == 0) {
            response.code(400).message("You need to provide at least one change");
            return;
        }

        serverManager.refreshServers();
        response.message(String.format("%d changes have been successfully applied", totalChanges));
    }

    @Override
    public void delete(Request request, ResponseController response) throws Exception {
        if (!isStringInBody(request, response, "uuid")) return;

        if (serverManager.getServer(getStringFromBody(request, "uuid")) == null) {
            response.code(404).message("The server does not exist");
            return;
        }

        serverManager.stopServer(serverManager.getServer(getStringFromBody(request, "uuid")));

        FileUtils.deleteDirectory(MCDashWrapper.getDataSource("servers/" + getStringFromBody(request, "uuid")));

        serverManager.refreshServers();

        response.message("The server has been successfully deleted");
    }
}
