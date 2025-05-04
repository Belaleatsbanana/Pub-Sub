package common;

import javax.swing.*;
import java.io.*;
import java.util.Objects;

public class PublisherInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final ImageIcon icon;

    public PublisherInfo(String id, String name, String description, ImageIcon icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublisherInfo)) return false;
        PublisherInfo that = (PublisherInfo) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
