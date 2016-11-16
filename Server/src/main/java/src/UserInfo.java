/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lenovo
 */
public class UserInfo {
    private String userName;
    private String password;
    private String rand;
    private List<Role> roles = new ArrayList<Role>();

    public UserInfo(String userName, String password, String rand) {
        this.userName = userName;
        this.password = password;
        this.rand = rand;
    }


    public String getUserName() {
        return userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the rand
     */
    public String getRand() {
        return rand;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean isValid(String operation) {
        for (Role role : roles) {
            if (role.hasPermission(operation))
                return true;
        }
        return false;
    }

}
