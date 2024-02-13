package de.gnmyt.mcdash.api.installer;

import de.gnmyt.mcdash.api.Logger;
import de.gnmyt.mcdash.api.ServerVersionManager;

import java.io.File;
import java.util.ArrayList;

public class SpigotInstaller implements VersionInstaller {

    private static final Logger LOG = new Logger(SpigotInstaller.class);

    private final ArrayList<String> SPIGOT_URLS = new ArrayList<>();

    public SpigotInstaller() {
        SPIGOT_URLS.add("https://download.getbukkit.org/spigot/spigot-%s.jar");
        SPIGOT_URLS.add("https://cdn.getbukkit.org/spigot/spigot-%s.jar");
        SPIGOT_URLS.add("https://cdn.getbukkit.org/spigot/spigot-%s-R0.1-SNAPSHOT-latest.jar");
    }

    @Override
    public boolean installVersion(String software, String version) {
        try {
            File file = new File(ServerVersionManager.getVersionFolder(), software + "-" + version + ".jar");

            if (file.exists()) {
                LOG.info("The version " + version + " of the software " + software + " is already installed");
                return true;
            }

            for (String url : SPIGOT_URLS) {
                String downloadUrl = String.format(url, version);

                if (downloadFile(downloadUrl, file)) {
                    LOG.info("The version " + version + " of the software " + software + " has been installed");
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            LOG.error("An error occurred while installing the spigot version", e);
            return false;
        }
    }
}
