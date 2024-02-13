package de.gnmyt.mcdash.routes.server;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.ServerManager;
import de.gnmyt.mcdash.entities.ServerStatus;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

public class ServerStartRoute extends DefaultHandler {

    public static final ServerManager serverManager = MCDashWrapper.getServerManager();

    /**
     * Validates the request and returns the uuid of the server
     *
     * @param request  the request
     * @param response the response
     * @return the uuid of the server
     */
    public static String validateRequest(Request request, ResponseController response) {
        if (!request.getBody().containsKey("uuid")) {
            response.code(400).message("You have to provide a uuid in the body of your request");
            return null;
        }

        String uuid = request.getBody().get("uuid");

        if (serverManager.getServer(uuid) == null) {
            response.code(404).message("Server not found");
            return null;
        }

        return uuid;
    }

    @Override
    public String path() {
        return "start";
    }

    @Override
    public void post(Request request, ResponseController response) throws Exception {
        String uuid = validateRequest(request, response);
        if (uuid == null) return;

        if (serverManager.getServer(uuid).getStatus() == ServerStatus.ONLINE) {
            response.code(400).message("The server is already running");
            return;
        }

        serverManager.startServer(serverManager.getServer(uuid));

        response.message("The server has been started successfully");
    }
}
