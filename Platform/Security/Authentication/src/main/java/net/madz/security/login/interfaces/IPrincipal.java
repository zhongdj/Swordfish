/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.interfaces;

import java.util.List;

/**
 * 
 * @author Administrator
 */
public interface IPrincipal {

	public String[] findGroup();

	public boolean isFrozen();

	ICompany findCompany();

	String getName();

	String getEncryptedPassword();

	boolean isLocked();

	int getInvalidPasswordAttempts();

	double getlockedDays();

	double getPasswordLife();

	List<String> getOldPasswords();

	void lock();

	void unlock();

	void resetPassword(String oldPassword, String newPassword, String confirmPassword);

	void handleLoginFailed();

	void handleLoginSuccess();

	void save();
}
