package Subscriber;

import common.Message;
import common.PublisherInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentIMP extends UnicastRemoteObject implements StudentIF {

    private Map<PublisherInfo, List<Message>> inbox = new HashMap<>();
    private transient StudentGUI studentGUI;

    public StudentIMP(StudentGUI studentGUI) throws RemoteException {
        super();
        this.studentGUI = studentGUI;
    }

    @Override
    public void receiveNotification(PublisherInfo publisher, Message message) throws RemoteException {
        inbox.computeIfAbsent(publisher, k -> new ArrayList<>()).add(message);
//        studentGUI.onNewMessage(publisher, message);
    }


    public Map<PublisherInfo, List<Message>> getInbox() throws RemoteException {
        return inbox;
    }
}
