package de.gnmyt.mcdash.entities;

public class User {

    private final String username;
    private final String password;

    /**
     * Basic constructor of the user
     *
     * @param username The username of the user
     * @param password The password of the user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username of the user
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

}
