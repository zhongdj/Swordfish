/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.madz.security.login.exception;

/**
 * 
 * @author Administrator
 */
public class NeedResetPasswordException extends Exception {

	private static final long serialVersionUID = 2551741834995417528L;

	public NeedResetPasswordException(String string) {
		super(string);
	}

	public NeedResetPasswordException() {
		super();
	}
}
