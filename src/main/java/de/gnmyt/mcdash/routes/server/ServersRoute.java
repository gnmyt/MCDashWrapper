package de.gnmyt.mcdash.routes.server;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.entities.Server;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static de.gnmyt.mcdash.routes.server.ServerStartRoute.serverManager;

public class ServersRoute extends DefaultHandler {

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
