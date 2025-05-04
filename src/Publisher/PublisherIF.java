package Publisher;

import common.StudentInfo;

import java.rmi.*;
import java.util.List;

// why did we create publisher interface even tho its does not contain any functions?
// because we want to use it as a marker interface
// and also to make it easier to implement in the future
public interface PublisherIF extends Remote {

}
