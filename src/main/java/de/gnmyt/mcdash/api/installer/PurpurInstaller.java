package de.gnmyt.mcdash.api.installer;

import de.gnmyt.mcdash.api.Logger;
import de.gnmyt.mcdash.api.ServerVersionManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;

public class PurpurInstaller implements VersionInstaller {

    private static final Logger LOG = new Logger(PurpurInstaller.class);
    private static final OkHttpClient client = new OkHttpClient();

    private static final String PURPUR_URL = "https://api.purpurmc.org/v2/purpur/%s/latest/download";

    @Override
    public boolean isValidVersion(String software, String version) {
        try {
            Request request = new Request.Builder().url(String.format(PURPUR_URL, version)).build();
            Response response = client.newCall(request).execute();

            return response.code() == 200;
        } catch (Exception e) {
            LOG.error("An error occurred while checking the purpur version", e);
            return false;
        }
    }

    @Override
    public boolean installVersion(String software, String version) {
        try {
            File file = new File(ServerVersionManager.getVersionFolder(), software + "-" + version + ".jar");

            if (file.exists()) {
                LOG.info("The version " + version + " of the software " + software + " is already installed");
                return true;
            }

            String downloadUrl = String.format(PURPUR_URL, version);

            if (downloadFile(downloadUrl, file)) {
                LOG.info("The version " + version + " of the software " + software + " has been installed");
                return true;
            }

            return false;
        } catch (Exception e) {
            LOG.error("An error occurred while installing the purpur version", e);
            return false;
        }
    }

}
