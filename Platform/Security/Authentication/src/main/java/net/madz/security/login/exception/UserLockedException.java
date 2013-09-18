/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.exception;

/**
 * 
 * @author Administrator
 */
public class UserLockedException extends Exception {

	private static final long serialVersionUID = 6081247937817247458L;

	public UserLockedException() {
		super();
	}

	public UserLockedException(String msg) {
		super(msg);
	}
}
