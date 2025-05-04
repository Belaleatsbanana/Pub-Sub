package common;
import java.io.*;

public class StudentInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;

    public StudentInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
