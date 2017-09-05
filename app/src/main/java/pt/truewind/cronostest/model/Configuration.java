package pt.truewind.cronostest.model;

/**
 * Created by mario.viegas on 10/11/2016.
 */

public class Configuration {

    public static final String TABLE_NAME = "configuration";

    private int id;
    private String name;
    private String value;

    public Configuration() {
    }

    public Configuration(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
