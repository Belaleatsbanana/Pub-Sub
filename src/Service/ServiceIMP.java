package Service;

import Publisher.PublisherIF;
import Service.records.PublisherRecord;
import Service.records.StudentRecord;
import Subscriber.StudentIF;
import common.Message;
import common.PublisherInfo;
import common.StudentInfo;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;

public class ServiceIMP extends UnicastRemoteObject implements ServiceIF {
    // core data
    private final Map<String, StudentRecord> students   = new HashMap<>();
    private final Map<String, PublisherRecord> publishers = new HashMap<>();

    // recent notifications queue (store last 100)
    private final Deque<NotificationEntry> recentNotifications = new ArrayDeque<>();
    private final int MAX_NOTIFS = 100;

    // event logs
    private final List<String> logs = new ArrayList<>();

    // Service Listeners
    private final List<ServiceListener> listeners = new ArrayList<>();

    public ServiceIMP() throws RemoteException {
        super();
        logEvent("Service started");
    }

    // Listener Management
    public void addServiceListener(ServiceListener listener) {
            listeners.add(listener);
    }

    public void removeServiceListener(ServiceListener listener) {
            listeners.remove(listener);
    }

    private void notifyStudentsUpdate()
    {
        listeners.forEach(ServiceListener::onStudentUpdate);
    }

    private void notifyPublishersUpdate()
    {
        listeners.forEach(ServiceListener::onPublisherUpdate);
    }

    private void notifyNotificationUpdate()
    {
        listeners.forEach(ServiceListener::onNotificationUpdate);
    }

    private void notifyLogUpdate()
    {
        listeners.forEach(ServiceListener::onLogUpdate);
    }

    @Override
    public synchronized void registerStudent(StudentIF student, StudentInfo info) throws RemoteException {
        students.put(info.getId(), new StudentRecord(student, info));
        logEvent("Registered student " + info.getId());

        notifyStudentsUpdate();
        notifyLogUpdate();
    }

    @Override
    public synchronized void deregisterStudent(String studentId) throws RemoteException {
        students.remove(studentId);
        logEvent("Deregistered student " + studentId);

        notifyStudentsUpdate();
        notifyLogUpdate();
    }

    @Override
    public synchronized void registerPublisher(PublisherIF publisher, PublisherInfo info) throws RemoteException {
        publishers.put(info.getId(), new PublisherRecord(publisher, info));
        logEvent("Registered publisher " + info.getId());

        notifyPublishersUpdate();
        notifyLogUpdate();
    }

    @Override
    public synchronized void deregisterPublisher(String publisherId) throws RemoteException {
        publishers.remove(publisherId);
        logEvent("Deregistered publisher " + publisherId);

        notifyPublishersUpdate();
        notifyLogUpdate();
    }

    @Override
    public synchronized boolean isSubscribed(String studentId, String publisherId) throws RemoteException {
        StudentRecord record = students.get(studentId);
        return record != null && record.getSubs().contains(publisherId);
    }

    @Override
    public synchronized void subscribe(String studentId, String publisherId) throws RemoteException {
        StudentRecord record = students.get(studentId);
        if (record != null && publishers.containsKey(publisherId)) {
            record.getSubs().add(publisherId);
            logEvent("Student " + studentId + " subscribed to " + publisherId);

            notifyLogUpdate();
        }
    }

    @Override
    public synchronized void unsubscribe(String studentId, String publisherId) throws RemoteException {
        StudentRecord record = students.get(studentId);
        if (record != null && publishers.containsKey(publisherId)) {
            record.getSubs().remove(publisherId);
            logEvent("Student " + studentId + " unsubscribed from " + publisherId);

            notifyLogUpdate();
        }
    }

    @Override
    public synchronized void publish(String publisherId, Message message) throws RemoteException {
        PublisherRecord pubRec = publishers.get(publisherId);
        if (pubRec == null) return;
        PublisherInfo info = pubRec.getInfo();

        // notify subscribers
        System.out.println("Publishing message from " + publisherId);
        for (StudentRecord rec : students.values()) {
            if (rec.getSubs().contains(publisherId)) {
                rec.getCallback().receiveNotification(info, message);
            }
        }
        addNotification(new NotificationEntry(publisherId, message));
        logEvent("Publisher " + publisherId + " published message: " + message.getContent());

        notifyNotificationUpdate();
        notifyLogUpdate();
    }

    @Override
    public synchronized List<StudentInfo> getSubscribers(String publisherId) throws RemoteException {
        List<StudentInfo> subs = new ArrayList<>();
        for (StudentRecord rec : students.values()) {
            if (rec.getSubs().contains(publisherId)) {
                subs.add(rec.getInfo());
            }
        }
        return subs;
    }

    @Override
    public synchronized List<PublisherInfo> getAvailablePublishers() throws RemoteException {
        List<PublisherInfo> pubs = new ArrayList<>();
        for (PublisherRecord rec : publishers.values()) {
            pubs.add(rec.getInfo());
        }
        return pubs;
    }

    public Map<String, StudentRecord> getStudents() {
        return Collections.unmodifiableMap(students);
    }

    public Map<String, PublisherRecord> getPublishers() {
        return Collections.unmodifiableMap(publishers);
    }

    public List<NotificationEntry> getRecentNotifications() {
        return new ArrayList<>(recentNotifications);
    }

    public int getNotificationCount() {
        return recentNotifications.size();
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    private void logEvent(String event) {
        String timestamp = LocalDateTime.now().toString();
        logs.add(timestamp + " - " + event);

    }

    private void addNotification(NotificationEntry entry) {
        if (recentNotifications.size() >= MAX_NOTIFS) {
            recentNotifications.removeFirst();
        }
        recentNotifications.addLast(entry);
    }

    public static class NotificationEntry {
        private final String publisherId;
        private final Message message;

        public NotificationEntry(String publisherId, Message message) {
            this.publisherId = publisherId;
            this.message = message;
        }
        public String getPublisherId() { return publisherId; }
        public Message getMessage() { return message; }
    }

    public static void main(String[] args) {
        try {
            ServiceIMP service = new ServiceIMP();
            Registry r = LocateRegistry.createRegistry(1099);
            r.rebind("Service", service);
            System.out.println("Service started and bound to registry.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
