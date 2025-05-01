package Service.records;

import Publisher.PublisherIF;
import common.PublisherInfo;

public class PublisherRecord {
    private final PublisherIF callback;
    private final PublisherInfo info;

    public PublisherRecord(PublisherIF callback, PublisherInfo info) {
        this.callback = callback;
        this.info = info;
    }

    public PublisherIF getCallback() {
        return callback;
    }

    public PublisherInfo getInfo() {
        return info;
    }
}
