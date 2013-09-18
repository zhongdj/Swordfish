/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.impl;

import javax.security.auth.login.LoginException;

import net.madz.security.login.exception.NeedResetPasswordException;
import net.madz.security.login.exception.PasswordException;
import net.madz.security.login.exception.UserLockedException;
import net.madz.security.login.exception.UserNotExistException;
import net.madz.security.login.interfaces.IAuditPolicy;
import net.madz.security.login.interfaces.ICompany;
import net.madz.security.login.interfaces.IPasswordEncryptor;
import net.madz.security.login.interfaces.IPrincipal;
import net.madz.security.login.interfaces.IPrincipalAuditor;

/*
 * @author Administrator
 */
public class PrincipalAuditor implements IPrincipalAuditor {

	private IAuditPolicy auditPolicy;
	private IPasswordEncryptor encryptor;
	private static volatile PrincipalAuditor instance;

	private PrincipalAuditor(IPasswordEncryptor encryptor, IAuditPolicy auditPolicy) {
		this.encryptor = encryptor;
		this.auditPolicy = auditPolicy;
	}

	public static PrincipalAuditor getInstance(IPasswordEncryptor encryptor, IAuditPolicy auditPolicy) {
		if (instance == null) {
			synchronized (PrincipalAuditor.class) {
				if (instance == null) {
					instance = new PrincipalAuditor(encryptor, auditPolicy);
				}
			}
		}
		return instance;
	}

	public boolean auditPassword(String password) throws PasswordException {
		// check whether the password matches the length requirement
		boolean result = false;
		if (auditPolicy.isPasswordMinLengthEnabled()) {
			if (password.length() < auditPolicy.getPasswordMinLength()) {
				throw new PasswordException("the password's length is too short, the min length of password is "
						+ auditPolicy.getPasswordMinLength());
			}
		}

		if (auditPolicy.isPasswordMaxLengthEnabled()) {
			if (password.length() > auditPolicy.getPasswordMaxLength()) {
				throw new PasswordException("the password's length is too long, the max length of password is "
						+ auditPolicy.getPasswordMaxLength());
			}
		}
		// check whether the password matches the pattern
		if (auditPolicy.isSpecialCharactersMustEnabled()) {
			char[] specialCharacters = auditPolicy.getSpecialCharacters();
			if (specialCharacters == null || specialCharacters.length <= 0) {
				return false;
			}
			for (int i = 0; i < password.length(); i++) {
				for (int j = 0; j < specialCharacters.length; j++) {
					if (password.charAt(i) == specialCharacters[j]) {
						result = true;
						break;
					} else {
						result = false;
					}
				}
				if (result) {
					break;
				}
			}
			if (!result) {
				throw new PasswordException("the password does not include any special characters in: "
						+ String.valueOf(auditPolicy.getSpecialCharacters()));
			}
		}

		if (auditPolicy.isCommonCharactersMustEnabled()) {
			char[] commonCharacters = auditPolicy.getCommonCharacters();
			if (commonCharacters == null || commonCharacters.length <= 0) {
				result = false;
			}
			for (int i = 0; i < password.length(); i++) {
				for (int j = 0; j < commonCharacters.length; j++) {
					if (password.charAt(i) == (commonCharacters[j])) {
						result = true;
						break;
					} else {
						result = false;
					}
				}
				if (result) {
					break;
				}
			}
			if (!result) {
				throw new PasswordException("the password does not include any common characters in: "
						+ String.valueOf(auditPolicy.getCommonCharacters()));
			}
		}
		if (auditPolicy.isDigitsMustEnabled()) {
			result = false;
			for (int i = 0; i < password.length(); i++) {
				try {
					Integer.parseInt(password.charAt(i) + "");
					result = true;
					break;
				} catch (NumberFormatException ex) {
				}
			}
			if (!result) {
				throw new PasswordException("the password does not include any digits");
			}
		}
		// make sure all the characters of password is leagel
		auditPasswordCharactersAllLeagel(password);
		return result;
	}

	public boolean auditPrincipal(IPrincipal principal) throws NeedResetPasswordException, PasswordException, UserLockedException {
		boolean result = false;
		double lifeLimit = 0;
		if (auditPolicy.isPasswordLifeAuditEnabled()) {
			lifeLimit = auditPolicy.getPasswordLifeLimit();
			if (principal.getPasswordLife() < lifeLimit) {
				result = true;
			} else {
				throw new NeedResetPasswordException("the password is overdue, need changed");
			}
		}

		if (principal.isLocked()) {
			throw new UserLockedException(principal.getName() + " has been locked!");
		}

		if (principal.isFrozen()) {
			throw new UserLockedException(principal.getName() + " has been frozen!");
		}
		return result;
	}

	/**
	 * 
	 * @param userName
	 * @param password
	 * @return String[] groupList
	 * @throws net.madz.security.login.exception.UserNotExistException
	 * @throws net.madz.security.login.exception.UserLockedException
	 * @exception IllegalStateException
	 *                if IPasswordProvider field is null
	 */
	public String[] authenticateUser(String userName, String password) throws UserNotExistException, UserLockedException, LoginException {
		IPrincipal principal = null;
		principal = Principal.findPrincipal(userName);

		if (principal == null) {
			throw new UserNotExistException();
		} else {
		}

		try {
			// authenticate principal's company, when needed
			if (AuditPolicyAdvisor.getInstance().isAuditCompanyEnabled()) {
				authenticateCompany(principal);
			}

			// authenticate user
			{
				if (principal.isLocked()) {
					if (auditPolicy.isAutoUnlockEnabled()) {
						if (principal.getlockedDays() >= auditPolicy.getUnlockInterval()) {
							principal.unlock();
						}
					}
					if (principal.isLocked()) {
						throw new UserLockedException();
					}
				}

				String encrypted = principal.getEncryptedPassword();
				String unvalidated = password;
				if (encryptor != null) {
					unvalidated = encryptor.encrypt(unvalidated);
				}

				if (encrypted.equals(unvalidated)) {
					principal.handleLoginSuccess();
					return principal.findGroup();
				} else {
					principal.handleLoginFailed();
					if (auditPolicy.isLockEnabled()) {
						if (principal.getInvalidPasswordAttempts() >= auditPolicy.getPasswordAttemptLimit()) {
							principal.lock();
							throw new UserLockedException();
						}
					}
					throw new LoginException("Password invalid");
				}
			}
		} finally {
			if (principal != null) {
				principal.save();
			}
		}
	}

	private boolean authenticateCompany(IPrincipal principal) throws UserLockedException {
		ICompany company = principal.findCompany();
		if (company.isFreezen()) {
			throw new UserLockedException(company.getName() + " has been frozen.");
		}

		if (company.isLocked()) {
			throw new UserLockedException(company.getName() + " has been locked.");
		}
		return true;
	}

	private void auditPasswordCharactersAllLeagel(String password) throws PasswordException {

		assert (password != null);
		assert (password.trim().length() > 0);

		StringBuffer passwordStr = new StringBuffer(password);
		if (auditPolicy.isSpecialCharactersMustEnabled()) {
			char[] specialCharacters = auditPolicy.getSpecialCharacters();
			for (int i = 0; i < passwordStr.length(); i++) {
				for (int j = 0; j < specialCharacters.length; j++) {
					if (passwordStr.charAt(i) == specialCharacters[j]) {
						passwordStr.deleteCharAt(i);
						i--;
					}
				}
			}
		}

		if (auditPolicy.isCommonCharactersMustEnabled()) {
			char[] commonCharacters = auditPolicy.getCommonCharacters();
			for (int i = 0; i < passwordStr.length(); i++) {
				for (int j = 0; j < commonCharacters.length; j++) {
					if (passwordStr.charAt(i) == (commonCharacters[j])) {
						passwordStr.deleteCharAt(i);
						i--;
					}
				}
			}
		}

		if (auditPolicy.isDigitsMustEnabled()) {
			for (int i = 0; i < passwordStr.length(); i++) {
				if (Character.isDigit(passwordStr.charAt(i))) {
					passwordStr.deleteCharAt(i);
					i--;
				}
			}
		}

		if (passwordStr.length() > 0) {
			System.out.println(passwordStr);
			StringBuffer sb = new StringBuffer();
			char[] charArray = auditPolicy.getSpecialCharacters();
			for (char a : charArray) {
				System.out.println(a);
				sb.append(a);
			}
			throw new PasswordException("Illegal character occurs in password, special characters are :" + sb.toString());
		}
	}
}
