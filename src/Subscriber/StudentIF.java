package Subscriber;

import common.Message;
import common.PublisherInfo;

import java.rmi.*;


public interface StudentIF extends Remote{
    public void receiveNotification(PublisherInfo publisher, Message message) throws RemoteException;
}
