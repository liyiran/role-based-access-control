package src;


import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author lenovo
 */
public interface RMI extends Remote {
    int login(String loginnum, String password) throws RemoteException;

    String print(String filename, String printer, String idNum, int rand) throws RemoteException;

    String queue(String idNum, int rand) throws RemoteException;

    void topQueue(int job, String idNum, int rand) throws RemoteException;

    void start(String idNum, int rand) throws RemoteException;

    void stop(String idNum, int rand) throws RemoteException;

    void restart(String idNum, int rand) throws RemoteException;

    void status(String idNum, int rand) throws RemoteException;

    String readConfig(String parameter, String idNum, int rand) throws RemoteException;

    String setConfig(String parameter, String value, String idNum, int rand) throws RemoteException;

}
