package de.gnmyt.mcdash.api;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.installer.PaperInstaller;
import de.gnmyt.mcdash.api.installer.PurpurInstaller;
import de.gnmyt.mcdash.api.installer.SpigotInstaller;
import de.gnmyt.mcdash.api.installer.VersionInstaller;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ServerVersionManager {

    private static final String PLUGIN_URL = "https://api.spiget.org/v2/resources/110687/download";

    private static final Logger LOG = new Logger(ServerVersionManager.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final File versionFolder = MCDashWrapper.getDataSource("versions");
    private static final File serverFolder = MCDashWrapper.getDataSource("servers");
    private final HashMap<String, VersionInstaller> installers = new HashMap<>();

    /**
     * Creates a new ServerVersionManager.
     * <p>
     * This will create the version folder if it doesn't exist
     * </p>
     */
    public ServerVersionManager() {
        installers.put("spigot", new SpigotInstaller());
        installers.put("purpur", new PurpurInstaller());
        installers.put("paper", new PaperInstaller());

        if (!versionFolder.exists() && !versionFolder.mkdirs()) {
            LOG.error("An error occurred while creating the version folder");
        }
    }

    /**
     * Installs a specific version of the minecraft server software
     *
     * @param software the software
     * @param version  the version
     */
    public boolean installVersion(String software, String version) {
        if (!installers.containsKey(software)) {
            LOG.error("There is no installer for the software: " + software);
            return false;
        }

        return installers.get(software).installVersion(software, version);
    }

    /**
     * Gets the path of a specific version of a software
     *
     * @param software the software
     * @param version  the version
     * @return the path of the version
     */
    public String getPath(String software, String version) {
        File file = new File(versionFolder, software + "-" + version + ".jar");

        if (!file.exists()) {
            if (!installVersion(software, version)) {
                LOG.error("An error occurred while installing the version " + version + " of the software " + software);
                return null;
            }
            return getPath(software, version);
        }

        return file.getAbsolutePath();
    }

    /**
     * Deletes a specific version of a software
     * @param software the software
     * @param version the version
     */
    public void deleteVersion(String software, String version) {
        File file = new File(versionFolder, software + "-" + version + ".jar");
        if (file.exists()) {
            file.delete();
        }
    }

    public void installPlugin(String uuid) {
        File file = new File(serverFolder, uuid);
        if (!file.exists()) {
            LOG.error("The server does not exist");
            return;
        }
        File pluginsFolder = new File(file, "plugins");
        if (!pluginsFolder.exists()) pluginsFolder.mkdirs();

        File plugin = new File(pluginsFolder, "MCDash.jar");
        if (plugin.exists()) plugin.delete();

        Request request = new Request.Builder().url(PLUGIN_URL).build();
        try (Response response = client.newCall(request).execute()) {
            FileUtils.copyInputStreamToFile(response.body().byteStream(), plugin);
        } catch (IOException e) {
            LOG.error("An error occurred while installing the plugin: " + e.getMessage());
        }
    }

    /**
     * Sets up the plugin
     * @param uuid the uuid
     * @param port the port
     * @param token the token
     */
    public void setupPlugin(String uuid, int port, String token) {
        File file = new File(serverFolder, uuid);
        if (!file.exists()) {
            LOG.error("The server does not exist");
            return;
        }
        File pluginsFolder = new File(file, "plugins");
        if (!pluginsFolder.exists()) pluginsFolder.mkdirs();

        File config = new File(pluginsFolder, "MinecraftDashboard/config.yml");
        if (config.exists()) config.delete();
        try {
            FileUtils.writeStringToFile(config, "port: " + port, "UTF-8");
        } catch (IOException e) {
            LOG.error("An error occurred while setting up the plugin: " + e.getMessage());
        }

        File accounts = new File(pluginsFolder, "MinecraftDashboard/accounts.yml");
        if (accounts.exists()) accounts.delete();
        try {
            FileUtils.writeStringToFile(accounts, "accounts:\n  " + token, "UTF-8");
        } catch (IOException e) {
            LOG.error("An error occurred while setting up the plugin: " + e.getMessage());
        }
    }

    /**
     * Gets the version folder
     *
     * @return the version folder
     */
    public static File getVersionFolder() {
        return versionFolder;
    }

    /**
     * Gets the installers
     * @return the installers
     */
    public HashMap<String, VersionInstaller> getInstallers() {
        return installers;
    }
}
