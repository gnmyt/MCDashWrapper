package de.gnmyt.mcdash.routes;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.UserManager;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

public class SetupRoute extends DefaultHandler {

    private final UserManager userManager = MCDashWrapper.getUserManager();

    @Override
    public String path() {
        return "setup";
    }

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        if (userManager.isSetupMode()) {
            response.json().add("setup", true).add("message", "The server is in setup mode").finish();
            return;
        }

        response.json().add("setup", false).finish();
    }

    @Override
    public void post(Request request, ResponseController response) throws Exception {
        if (!userManager.isSetupMode()) {
            response.code(403).message("The server is not in setup mode");
            return;
        }

        if (!isStringInBody(request, response, "username") || !isStringInBody(request, response, "password")) return;

        String username = getStringFromBody(request, "username");
        String password = getStringFromBody(request, "password");

        userManager.addUser(username, password);
        response.message("The user has been successfully created");
    }

}
