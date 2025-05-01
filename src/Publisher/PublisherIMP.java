package Publisher;

import common.StudentInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PublisherIMP extends UnicastRemoteObject implements PublisherIF {
    public PublisherIMP() throws RemoteException {
        super();
    }
}
