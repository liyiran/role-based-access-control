package src;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author lenovo
 */
public class ServerRMI extends UnicastRemoteObject implements RMI {
    private static final String STOP = "stop";
    private static final String RESTART = "restart";
    private static final String STATUS = "status";
    private static final String READ_CONFIG = "readConfig";
    private static final String SET_CONFIG = "setConfig";
    private static final String PRINT = "print";
    private static final String QUEUE = "queue";
    private static final String TOP_QUEUE = "topQueue";
    private static final String START = "start";
    private static Logger logger = Logger.getLogger(ServerRMI.class.getName());
    private Map<String, UserInfo> userInfos = new HashMap<String, UserInfo>();
    private Map<String, Integer> sessions = new HashMap<String, Integer>();
    private Map<String, Role> roles = new HashMap<String, Role>();//role name -> roleObject
    private String baseDir = "";

    public ServerRMI() throws IOException {
        super();
        baseDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static void main(String args[]) {
        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("server", new ServerRMI());
            logger.info("Server started");
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    private boolean validateUser(String userName, Integer sessionId, String operation) throws RemoteException {
        //check if the user is in the session
        Integer sessionIdLocal = sessions.get(userName);
        if (sessionIdLocal == null || !sessionIdLocal.equals(sessionId)) {
            //user is not in session
            throw new RemoteException("User not Login");
        }
        UserInfo userInfo = userInfos.get(userName);
        assert userInfo != null;
        if (!userInfo.isValid(operation)) {
            throw new RemoteException("user has no privilege!");
        }
        return true;
    }

    @Override
    public String print(String filename, String printer, String userName, int sessionId) throws RemoteException {
        if (validateUser(userName, sessionId, PRINT)) {
            logger.info("The user is authenticated in print operation!");
            return filename + printer;
        } else {
            return null;
        }
    }


    @Override
    public String queue(String userName, int sessionId) throws RemoteException {
        if (validateUser(userName, sessionId, QUEUE)) {
            logger.info("lists the print queue on the user's display");
            String job = "<1>";
            String file = "<word1>";
            return job + file;
        } else {
            return null;
        }

    }

    @Override
    public void topQueue(int job, String userName, int sessionId) throws RemoteException {
        if (validateUser(userName, sessionId, TOP_QUEUE)) {
            logger.info("moves job to the top");
        }
    }

    @Override
    public void start(String userName, int sessionId) throws RemoteException {
        if (validateUser(userName, sessionId, START)) {
            logger.info("starts the print server");
        }
    }

    @Override
    public void stop(String userName, int sessionId) throws RemoteException {
        if (validateUser(userName, sessionId, STOP)) {
            logger.info("stops the print server");
        }

    }

    @Override
    public void restart(String userName, int sessionId) throws RemoteException {
        if (validateUser(userName, sessionId, RESTART)) {
            logger.info("restart the print server");
        }
    }

    @Override
    public void status(String userName, int sessionId) throws RemoteException {
        if (validateUser(userName, sessionId, STATUS)) {
            logger.info("check the print status");
        }
    }

    @Override
    public String readConfig(String parameter, String idNum, int rand) throws RemoteException {
        if (validateUser(idNum, rand, READ_CONFIG)) {
            logger.info("read config");
            return "prints the value of the parameter on user's display";
        }
        return null;
    }

    @Override
    public String setConfig(String parameter, String value, String idNum, int rand) throws RemoteException {
        if (validateUser(idNum, rand, SET_CONFIG)) {
            logger.info("set config");
            return "set the parameter to value";
        }
        return null;
    }

    @Override
    public int login(String loginNum, String password) throws RemoteException {

        try {

            if (roles.isEmpty()) {
                //从CSV中取出对应内容，存到UserInfo类中
                FileReader in = new FileReader(baseDir + "/role-permission.csv");
                Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("roleName", "permission").parse(in);
                for (CSVRecord record : records) {
                    Role role = new Role();
                    role.setRoleName(record.get("roleName"));
                    roles.put(role.getRoleName(), role);
                    String[] permissions = StringUtils.split(record.get("permission"));
                    for (String permission : permissions) {
                        role.addPermission(permission);
                    }
                }
                in.close();
            }

            if (userInfos.isEmpty()) {
                //从CSV中取出对应内容，存到UserInfo类中
                FileReader in = new FileReader(baseDir + "/password.csv");
                Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("userName", "password", "salt").parse(in);
                for (CSVRecord record : records) {
                    UserInfo userInfo = new UserInfo(record.get("userName"), record.get("password"), record.get("salt"));
                    userInfos.put(userInfo.getUserName(), userInfo);
                }
                in.close();
                in = new FileReader(baseDir + "/user-role.csv");
                records = CSVFormat.RFC4180.withHeader("userName", "role").parse(in);
                for (CSVRecord record : records) {
                    String roleNames[] = StringUtils.split(record.get("role"));
                    UserInfo userInfo = userInfos.get(record.get("userName"));
                    assert userInfo != null;
                    for (String roleName : roleNames) {
                        Role role = roles.get(roleName);
                        assert role != null;
                        userInfo.addRole(role);
                    }
                }
                in.close();
            }

            //判断用户信息是否在本地文件
            UserInfo user = userInfos.get(loginNum);
            if (user == null) {
                logger.warning("User information is not in the file!");
                throw new RemoteException("User information is not in the file!");
            }
            //生成MD5密码
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(password.concat(user.getRand()).getBytes());
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashText = bigInt.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            //生成rand
            if (hashText.equals(user.getPassword())) {
                Integer sessionID = RandomUtils.nextInt();
                sessions.put(loginNum, sessionID);
                logger.info("Login succeed!");
                return sessionID;
            } else {
                //throw new RemoteException("wrong pw");
                logger.warning("wrong pw");
                return 0;
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerRMI.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException("IO exception");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ServerRMI.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException("NoSuchAlgorithmException");
        }
    }
}
