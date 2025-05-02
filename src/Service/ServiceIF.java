package Service;

import Publisher.PublisherIF;
import Subscriber.StudentIF;
import common.Message;
import common.PublisherInfo;
import common.StudentInfo;

import java.rmi.*;
import java.util.List;

public interface ServiceIF extends Remote {

    void registerStudent(StudentIF student, StudentInfo info) throws RemoteException;
    void deregisterStudent(String studentId) throws RemoteException;

    void registerPublisher(PublisherIF publisher, PublisherInfo info) throws RemoteException;
    void deregisterPublisher(String publisherId) throws RemoteException;

    void subscribe(String studentId, String publisherId) throws RemoteException;
    void unsubscribe(String studentId, String publisherId) throws RemoteException;

    void publish(String publisherId, Message message) throws RemoteException;

    List<StudentInfo> getSubscribers(String publisherId) throws RemoteException;
    List<PublisherInfo> getAvailablePublishers() throws RemoteException;
}
