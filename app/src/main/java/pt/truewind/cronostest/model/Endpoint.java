package pt.truewind.cronostest.model;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class Endpoint {

    public static final String TABLE_NAME = "endpoint";

    private int id;
    private String token;
    private String username;
    private String password;

    public Endpoint() {
    }

    public Endpoint(String token) {
        this.token = token;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
