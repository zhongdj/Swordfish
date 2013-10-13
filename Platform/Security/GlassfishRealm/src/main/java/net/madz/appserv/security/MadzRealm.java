/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.appserv.security;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import net.madz.security.login.exception.UserLockedException;
import net.madz.security.login.exception.UserNotExistException;
import net.madz.security.login.factory.EncryptorFactory;
import net.madz.security.login.impl.AuditPolicyAdvisor;
import net.madz.security.login.impl.Principal;
import net.madz.security.login.impl.PrincipalAuditor;
import net.madz.security.login.impl.RealmProperties;
import net.madz.security.login.interfaces.IPasswordEncryptor;

import com.iplanet.ias.security.auth.realm.IASRealm;
import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;

/**
 * 
 * @author CleaNEr
 */
public class MadzRealm extends AppservRealm {

	public static final String AUTH_TYPE = "Madz JDBC Realm";
	private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
	private static final ThreadLocal<ErrorCode> LOGIN_FAILURE = new ThreadLocal<>();

	public static enum ErrorCode {
		UsernameNotExist, PasswordInvalid, UserHasBeenLocked, UserHasBeenFrozen, TenantHasBeenLocked, TenantHasBeenFrozen
	}

	@Override
	protected void init(Properties props) throws BadRealmException,
			NoSuchRealmException {
		super.init(props);
		String jaasCtx = props.getProperty(IASRealm.JAAS_CONTEXT_PARAM);

		// TODO:implements init properties of MadzRealm
		{
			if (jaasCtx == null) {
				String msg = "JAAS_CONTEXT_PARAM is null";
				throw new BadRealmException(msg);
			} else {
				this.setProperty(IASRealm.JAAS_CONTEXT_PARAM, jaasCtx);
				if (_logger.isLoggable(Level.FINE)) {
					_logger.fine("MadzRealm : " + IASRealm.JAAS_CONTEXT_PARAM
							+ "=" + jaasCtx);
				}
			}
		}

		RealmProperties.setConfigurations(props);
		AuditPolicyAdvisor.setConfigurations(props);

	}

	public String[] authenticate(String username, String password)
			throws LoginException {

		USERNAME.set(username);

		if (_logger.isLoggable(Level.FINE)) {
			_logger.fine("MadzRealm is authenticating " + username + "...");
		}

		EncryptorFactory encryptorFactory = EncryptorFactory.getInstance();
		IPasswordEncryptor encryptor = encryptorFactory.getPasswordEncryptor();
		AuditPolicyAdvisor auditPolicy = AuditPolicyAdvisor.getInstance();
		PrincipalAuditor instance = PrincipalAuditor.getInstance(encryptor,
				auditPolicy);
		try {
			String[] groups = instance.authenticateUser(username, password);

			if (_logger.isLoggable(Level.FINE)) {
				_logger.fine(username + " is authenticated Successfully");
			}
			
			return groups;
		} catch (UserNotExistException ex) {

			LOGIN_FAILURE.set(ErrorCode.UsernameNotExist);

			if (_logger.isLoggable(Level.WARNING)) {
				_logger.fine(username + " is authenticated Failed");
			}
			Logger.getLogger(MadzRealm.class.getName()).log(Level.SEVERE, null,
					ex);
			LoginException e = new LoginException();
			e.initCause(ex);
			throw e;
		} catch (UserLockedException ex) {
			LOGIN_FAILURE.set(ErrorCode.UserHasBeenLocked);

			if (_logger.isLoggable(Level.WARNING)) {
				_logger.fine(username + " is authenticated Failed");
			}
			Logger.getLogger(MadzRealm.class.getName()).log(Level.SEVERE, null,
					ex);
			LoginException e = new LoginException();
			e.initCause(ex);
			throw e;
		}

	}

	@Override
	public String getAuthType() {
		return AUTH_TYPE;
	}

	@Override
	public Enumeration<String> getGroupNames(String username)
			throws InvalidOperationException, NoSuchUserException {
		try {

			String[] groups = Principal.findPrincipal(username).findGroup();
			Vector<String> list = new Vector<String>();
			for (int i = 0; i < groups.length; i++) {
				list.add(groups[i]);
			}
			return list.elements();
		} catch (UserNotExistException ex) {
			throw new NoSuchUserException(username + " does not exist");
		}
	}

	public static String getUsername() {
		return USERNAME.get();
	}

	public static ErrorCode getLoginFailure() {
		return LOGIN_FAILURE.get();
	}

	public static void clearThreadLocals() {
		USERNAME.remove();
		LOGIN_FAILURE.remove();
	}
}
