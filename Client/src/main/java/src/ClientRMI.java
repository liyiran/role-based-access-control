package src;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author lenovo
 */
public class ClientRMI {
    public static void main(String args[]) {
        ClientRMI client = new ClientRMI();
        client.connectServer();


    }

    private void connectServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
            RMI rmi = (RMI) reg.lookup("server");
            System.out.println("Connected to server");
            System.out.println("user ID:");
            Scanner in = new Scanner(System.in);
            String idNum = in.nextLine();
            System.out.println("password:");
            String password = in.nextLine();

            int session = rmi.login(idNum, password);
            String queue = rmi.queue(idNum, session);
//            String print = rmi.print("work1", "printer1",idNum,session);
//            rmi.topQueue(2,idNum,session);
//            rmi.start(idNum,session);
            rmi.stop(idNum, session);
//            rmi.restart(idNum,session);
//            rmi.status(idNum,session);
//            rmi.readConfig(print,idNum,session);
//            rmi.setConfig(print, queue,idNum,session);
            System.out.println(queue);

        } catch (RemoteException | NotBoundException e) {
            System.out.println(e);
        }
    }
}
