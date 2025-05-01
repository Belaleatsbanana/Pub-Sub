package common;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String content;
    private final LocalDateTime timestamp;

    public Message(String content, LocalDateTime timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
