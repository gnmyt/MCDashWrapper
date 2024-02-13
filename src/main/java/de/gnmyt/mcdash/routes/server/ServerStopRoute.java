package de.gnmyt.mcdash.routes.server;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.ServerManager;
import de.gnmyt.mcdash.entities.ServerStatus;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

public class ServerStopRoute extends DefaultHandler {

    private final ServerManager serverManager = MCDashWrapper.getServerManager();

    @Override
    public String path() {
        return "stop";
    }


    @Override
    public void post(Request request, ResponseController response) throws Exception {
        String uuid = ServerStartRoute.validateRequest(request, response);
        if (uuid == null) return;

        if (serverManager.getServer(uuid).getStatus() == ServerStatus.OFFLINE) {
            response.code(400).message("The server is already stopped");
            return;
        }

        serverManager.stopServer(serverManager.getServer(uuid));

        response.message("The server has been stopped successfully");
    }
}
