package Subscriber;

import common.Message;
import common.PublisherInfo;
import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StudentIMP extends UnicastRemoteObject implements StudentIF {
    private final Map<PublisherInfo, Deque<Message>> inbox = new ConcurrentHashMap<>();
    private transient StudentGUI studentGUI;
    private static final int MAX_MESSAGES_PER_PUBLISHER = 100;

    public StudentIMP(StudentGUI studentGUI) throws RemoteException {
        super();
        this.studentGUI = studentGUI;
    }

    @Override
    public void receiveNotification(PublisherInfo publisher, Message message) throws RemoteException {
        System.out.println("Received notification from " + publisher.getName());
        inbox.compute(publisher, (k, v) -> {
            if (v == null) v = new ArrayDeque<>(MAX_MESSAGES_PER_PUBLISHER + 1);
            v.addFirst(message);
            while (v.size() > MAX_MESSAGES_PER_PUBLISHER) v.removeLast();
            return v;
        });

        if (studentGUI != null) {
            SwingUtilities.invokeLater(() ->
                    studentGUI.handleNewNotification(publisher, message)
            );
        }
    }

    // GUI access method
    public List<PublisherInfo> getSubscribedPublishers() {
        return new ArrayList<>(inbox.keySet());
    }

    public List<Message> getMessagesForPublisher(PublisherInfo publisher) {
        Deque<Message> messages = inbox.get(publisher);
        return messages != null ? new ArrayList<>(messages) : Collections.emptyList();
    }

    public void cleanup() {
        inbox.clear();
        studentGUI = null;
    }
}