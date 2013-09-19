/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.factory;

/**
 * 
 * @author Administrator
 */
public class BadRealmException extends RuntimeException {

    private static final long serialVersionUID = -8166922095886126273L;

    public BadRealmException(String msg) {
        super(msg);
    }
}
