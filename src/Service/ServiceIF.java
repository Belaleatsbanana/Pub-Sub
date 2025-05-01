package Service;

import Publisher.PublisherIF;
import Subscriber.StudentIF;

import java.rmi.*;

public interface ServiceIF extends Remote {

    public void registerStudent(StudentIF student) throws RemoteException;
    public void registerPublisher(PublisherIF publisher) throws RemoteException;

    public void publish(String msg) throws RemoteException;
    public void subscribe(String msg) throws RemoteException;
    public void unsubscribe(String msg) throws RemoteException;

    public void deregisterStudent(StudentIF student) throws RemoteException;
    public void deregisterPublisher(PublisherIF publisher) throws RemoteException;

}
