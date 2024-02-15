package de.gnmyt.mcdash.api;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.entities.User;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class UserManager {

    private static final Logger LOG = new Logger(UserManager.class);
    private static final File USERS_FILE = MCDashWrapper.getDataSource("users.json");

    private ArrayList<User> users = new ArrayList<>();
    private final Gson gson = new Gson();

    public UserManager() {
        if (!USERS_FILE.exists()) {
            try {
                USERS_FILE.createNewFile();
                FileUtils.writeStringToFile(USERS_FILE, "[]", "UTF-8");
            } catch (Exception e) {
                LOG.error("An error occurred while creating the users file: {}", e.getMessage());
            }
        }

        readUsers();
    }

    /**
     * Checks if the server is in setup mode
     *
     * @return <code>true</code> if the server is in setup mode
     */
    public boolean isSetupMode() {
        return users.isEmpty();
    }

    /**
     * Adds a new user to the user list
     *
     * @param username The username of the user
     * @param password The password of the user. It will be hashed with BCrypt
     */
    public void addUser(String username, String password) {
        users.add(new User(username, BCrypt.withDefaults().hashToString(12, password.toCharArray())));
        saveUsers();
    }

    /**
     * Removes a user from the user list
     *
     * @param username The username of the user
     */
    public void removeUser(String username) {
        users.removeIf(user -> user.getUsername().equals(username));
        saveUsers();
    }

    /**
     * Checks if a user exists
     *
     * @param username The username of the user
     * @return <code>true</code> if the user exists
     */
    public boolean userExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    /**
     * Checks if the password of a user is correct
     *
     * @param username The username of the user
     * @param password The password of the user
     * @return <code>true</code> if the password is correct
     */
    public boolean isPasswordCorrect(String username, String password) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username) && BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified);
    }

    /**
     * Reads the users from the users file
     */
    public void readUsers() {
        try {
            String json = FileUtils.readFileToString(USERS_FILE, "UTF-8");
            users = gson.fromJson(json, new TypeToken<ArrayList<User>>() {
            }.getType());
        } catch (Exception e) {
            LOG.error("An error occurred while reading the users: {}", e.getMessage());
        }
    }

    /**
     * Saves the users to the users file
     */
    public void saveUsers() {
        try {
            String json = gson.toJson(users);
            FileUtils.writeStringToFile(USERS_FILE, json, "UTF-8");
        } catch (Exception e) {
            LOG.error("An error occurred while saving the users: {}", e.getMessage());
        }
    }
}
