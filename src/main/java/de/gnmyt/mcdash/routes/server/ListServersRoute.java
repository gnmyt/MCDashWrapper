package de.gnmyt.mcdash.routes.server;

import de.gnmyt.mcdash.entities.Server;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

import java.util.ArrayList;
import java.util.HashMap;

import static de.gnmyt.mcdash.routes.server.ServerStartRoute.serverManager;

public class ListServersRoute extends DefaultHandler {

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        ArrayList<HashMap<String, Object>> servers = new ArrayList<>();

        for (Server server : serverManager.getServers()) {
            HashMap<String, Object> serverObj = new HashMap<>();

            serverObj.put("uuid", server.getName());
            serverObj.put("status", server.getStatus().name());

            serverObj.put("configuration", server.getConfiguration().toHashMap());

            servers.add(serverObj);
        }

        response.jsonArray(servers);
    }
}
