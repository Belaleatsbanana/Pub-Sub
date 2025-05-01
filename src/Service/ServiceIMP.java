package Service;

import Publisher.PublisherIF;
import Service.records.PublisherRecord;
import Service.records.StudentRecord;
import Subscriber.StudentIF;
import common.Message;
import common.PublisherInfo;
import common.StudentInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceIMP extends UnicastRemoteObject implements ServiceIF {

    private final Map<String, StudentRecord> students   = new HashMap<>();
    private final Map<String, PublisherRecord> publishers = new HashMap<>();

    public ServiceIMP() throws RemoteException {
        super();
    }

    @Override
    public void registerStudent(StudentIF student, StudentInfo info) throws RemoteException{
        students.put(info.getId(), new StudentRecord(student, info));
        System.out.println("Registered student " + info.getId());
    }
    @Override
    public void deregisterStudent(String studentId) throws RemoteException{
        students.remove(studentId);
        System.out.println("Deregistered student " + studentId);
    }

    @Override
    public void registerPublisher(PublisherIF publisher, PublisherInfo info) throws RemoteException{
        publishers.put(info.getId(), new PublisherRecord(publisher, info));
    }
    @Override
    public void deregisterPublisher(String publisherId) throws RemoteException{
        publishers.remove(publisherId);
    }

    @Override
    public void subscribe(String studentId, String publisherId) throws RemoteException{
        StudentRecord record = students.get(studentId);
        if(record != null && publishers.get(publisherId) != null) {
            record.getSubs().add(publisherId);
            System.out.println("Subscribed to student " + studentId + " to publisher " + publisherId);
        }
    }
    @Override
    public void unsubscribe(String studentId, String publisherId) throws RemoteException{
        StudentRecord record = students.get(studentId);
        if(record != null && publishers.get(publisherId) != null) {
            record.getSubs().remove(publisherId);
            System.out.println("Unsubscribed from student " + studentId + " to publisher " + publisherId);
        }
    }

    @Override
    public void publish(String publisherId, Message message) throws RemoteException{
        PublisherInfo publisherRecord = publishers.get(publisherId).getInfo();

        for (StudentRecord record : students.values()) {
            if(record != null && publishers.get(publisherId) != null) {
                record.getCallback().receiveNotification(publisherRecord, message);
            }
        }
    }

    @Override
    public List<StudentInfo> getSubscribers(String publisherId) throws RemoteException {
        ArrayList<StudentInfo> subscribers = new ArrayList<>();
        for (StudentRecord record : students.values()) {
            if(record.getSubs().contains(publisherId)) {
                subscribers.add(record.getInfo());
            }
        }
        return subscribers;
    }

    public Map<String, StudentRecord> getStudents() {
        return students;
    }

    public Map<String, PublisherRecord> getPublishers() {
        return publishers;
    }
}
