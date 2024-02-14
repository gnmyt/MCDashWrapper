package de.gnmyt.mcdash.api;

import de.gnmyt.mcdash.MCDashWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.FileFilter;

public class JavaVersionManager {

    private static final Logger LOG = new Logger(JavaVersionManager.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final File javaFolder = MCDashWrapper.getDataSource("versions/jre");
    private static final String DL_URL = "https://api.adoptium.net/v3/binary/latest/%s/ga/%s/%s/jre/hotspot/normal/adoptium";

    /**
     * Basic constructor of the {@link JavaVersionManager}
     *
     * <p>
     * This will create the jre folder if it doesn't exist
     * </p>
     */
    public JavaVersionManager() {
        if (!javaFolder.exists() && !javaFolder.mkdirs()) {
            LOG.error("An error occurred while creating the java folder");
        }
    }


    /**
     * Gets the java version of a specific minecraft version
     *
     * @param minecraftVersion the minecraft version
     * @return the java version
     */
    public int getJavaVersion(String minecraftVersion) {
        int versionNumber = Integer.parseInt(minecraftVersion.split("\\.")[1]);

        if (versionNumber >= 17) return 17;
        if (versionNumber == 16) return 16;
        if (versionNumber >= 12) return 11;

        return 8;
    }

    /**
     * Gets the path of a specific java version
     * @param minecraftVersion the minecraft version
     * @return the path of the java version
     */
    public String getPath(String minecraftVersion) {
        File javaPath = new File(javaFolder, getJavaVersion(minecraftVersion) + "/bin/java"
                + (System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : ""));

        if (!javaPath.exists()) {
            LOG.info("The java version " + getJavaVersion(minecraftVersion) + " is not installed. Installing it now...");
            installJavaVersion(getJavaVersion(minecraftVersion));
        }

        return javaPath.getAbsolutePath();
    }

    /**
     * Installs a specific java version
     * @param version the version
     */
    public void installJavaVersion(int version) {
        String url = String.format(DL_URL, version, System.getProperty("os.name").toLowerCase().contains("win") ? "windows" : "linux",
                System.getProperty("os.arch").contains("64") ? "x64" : "x32");

        try {
            Response response = client.newCall(new Request.Builder().url(url).build()).execute();
            if (!response.isSuccessful()) {
                LOG.error("An error occurred while downloading the java version: " + response.message());
                return;
            }

            File javaFile = new File(javaFolder, version + (System.getProperty("os.name").toLowerCase().contains("win") ? ".zip" : ".tar.gz"));
            FileUtils.copyToFile(response.body().byteStream(), javaFile);

            Archiver archiver = ArchiverFactory.createArchiver(javaFile);
            archiver.extract(javaFile, javaFolder);

            javaFile.delete();

            FileFilter filter = pathname -> pathname.getName().contains("jre") && pathname.getPath().contains(version + "");

            File[] files = javaFolder.listFiles(filter);

            if (files.length == 0) {
                LOG.error("An error occurred while extracting the java version");
                return;
            }

            FileUtils.moveDirectory(files[0], new File(javaFolder, String.valueOf(version)));

            LOG.info("The java version " + version + " has been successfully installed");
        } catch (Exception e) {
            LOG.error("An error occurred while downloading the java version: " + e.getMessage());
        }
    }

}
