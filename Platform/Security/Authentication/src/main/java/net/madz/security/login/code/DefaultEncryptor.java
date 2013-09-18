/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.code;

import net.madz.security.login.interfaces.IPasswordEncryptor;

/**
 * 
 * @author Administrator
 */
public class DefaultEncryptor implements IPasswordEncryptor {

	public String encrypt(String password) {
		return password;
	}
}
