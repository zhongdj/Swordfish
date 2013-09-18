/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.interfaces;

import javax.security.auth.login.LoginException;

import net.madz.security.login.exception.NeedResetPasswordException;
import net.madz.security.login.exception.PasswordException;
import net.madz.security.login.exception.UserLockedException;
import net.madz.security.login.exception.UserNotExistException;

/**
 * 
 * @author Administrator
 */
public interface IPrincipalAuditor {

	/**
	 * The method authenticates user by userName and password when user try to
	 * login the system. If success, handle login success and return the users'
	 * groups. Otherwise, handle login failed and throw exception.
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws UserNotExistException
	 *             ,UserLockedException,LoginException
	 */
	String[] authenticateUser(String userName, String inputPassword)
			throws UserNotExistException, UserLockedException, LoginException;

	/**
	 * Audit password with configured password policy while creating new
	 * password, such as registering new users or reseting password
	 * 
	 * @param password
	 * @return
	 * @throws PasswordException
	 */
	boolean auditPassword(String password) throws PasswordException;

	/**
	 * Audit principal after login successfully.
	 * 
	 * @param principal
	 * @return
	 * @throws NeedResetPasswordException
	 *             if principal's password reached the password life
	 * @throws
	 */
	boolean auditPrincipal(IPrincipal principal)
			throws NeedResetPasswordException, PasswordException,
			UserLockedException;
}
