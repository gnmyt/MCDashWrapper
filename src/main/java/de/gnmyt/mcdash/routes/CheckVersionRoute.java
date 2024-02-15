package de.gnmyt.mcdash.routes;

import de.gnmyt.mcdash.api.ServerVersionManager;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

public class CheckVersionRoute extends DefaultHandler {

    private final static ServerVersionManager versionManager = new ServerVersionManager();

    @Override
    public String path() {
        return "check_version";
    }

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        if (!isStringInQuery(request, response, "software") || !isStringInQuery(request, response, "version")) return;

        String software = getStringFromQuery(request, "software");
        String version = getStringFromQuery(request, "version");

        if (!versionManager.getInstallers().containsKey(software)) {
            response.json().add("valid", false).finish();
            return;
        }

        response.json().add("valid", versionManager.getInstallers().get(software).isValidVersion(software, version)).finish();
    }
}
