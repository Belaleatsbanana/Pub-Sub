package Service.records;

import Subscriber.StudentIF;
import common.StudentInfo;

import java.util.HashSet;
import java.util.Set;


public class StudentRecord {
    private final StudentIF callback;
    private final StudentInfo info;
    private final Set<String> subs = new HashSet<>();

    public StudentRecord(StudentIF callback, StudentInfo info) {
        this.callback = callback;
        this.info = info;
    }

    public StudentIF getCallback() {
        return callback;
    }
    public StudentInfo getInfo() {
        return info;
    }
    public Set<String> getSubs() {
        return subs;
    }
}
