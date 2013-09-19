/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.impl;

import net.madz.security.login.interfaces.ICompany;

/**
 * 
 * @author CleaNEr
 */
public class Company implements ICompany {

    private String name;
    private boolean locked;
    private boolean freezen;

    public Company(String name, boolean locked, boolean freezen) {
        this.name = name;
        this.locked = locked;
        this.freezen = freezen;
    }

    public String getName() {
        return name;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isFreezen() {
        return freezen;
    }
}
