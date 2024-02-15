package de.gnmyt.mcdash.api.installer;

import com.google.gson.Gson;
import de.gnmyt.mcdash.api.Logger;
import de.gnmyt.mcdash.api.ServerVersionManager;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PaperInstaller implements VersionInstaller {

    private static final String PAPER_URL = "https://papermc.io/api/v2/projects/paper/versions/%s/builds/%s/downloads/paper-%s-%s.jar";
    private static final String PAPER_API = "https://papermc.io/api/v2/projects/paper/versions/%s";

    private final OkHttpClient client = new OkHttpClient();
    private final Logger LOG = new Logger(PaperInstaller.class);


    public String getPaperVersion(String version) {
        try {
            okhttp3.Request request = new okhttp3.Request.Builder().url(String.format(PAPER_API, version)).build();
            okhttp3.Response response = client.newCall(request).execute();

            if (response.code() == 200) {
                HashMap json = new Gson().fromJson(response.body().string(), HashMap.class);
                ArrayList builds = (ArrayList) json.get("builds");

                return builds.get(0).toString().split("\\.")[0];
            }

            response.close();
        } catch (Exception e) {
            LOG.error("An error occurred while getting the paper version", e);
        }

        return null;
    }

    @Override
    public boolean isValidVersion(String software, String version) {
        try {
            okhttp3.Request request = new okhttp3.Request.Builder().url(String.format(PAPER_API, version)).build();
            okhttp3.Response response = client.newCall(request).execute();

            boolean isValid = response.code() == 200;
            response.close();

            return isValid;
        } catch (Exception e) {
            LOG.error("An error occurred while checking the paper version", e);
            return false;
        }
    }

    @Override
    public boolean installVersion(String software, String version) {
        try {
            String paperVersion = getPaperVersion(version);
            if (paperVersion == null) {
                LOG.error("An error occurred while getting the paper version");
                return false;
            }

            String downloadUrl = String.format(PAPER_URL, version, paperVersion, version, paperVersion);
            return downloadFile(downloadUrl, new File(ServerVersionManager.getVersionFolder(), software + "-" + version + ".jar"));
        } catch (Exception e) {
            LOG.error("An error occurred while installing the paper version", e);
            return false;
        }
    }
}
