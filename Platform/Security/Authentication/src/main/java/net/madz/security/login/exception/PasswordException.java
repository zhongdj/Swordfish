/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.exception;

/**
 * 
 * @author Administrator
 */
public class PasswordException extends Exception {

	private static final long serialVersionUID = -5293332585507292084L;

	public PasswordException() {
		super();
	}

	public PasswordException(String info) {
		super(info);
	}
}
