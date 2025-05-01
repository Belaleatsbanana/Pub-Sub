package Subscriber;

import java.rmi.*;


public interface StudentIF extends Remote{
    public void receiveNotification(String msg) throws RemoteException;
    public void subscribe(String msg) throws RemoteException;
}
