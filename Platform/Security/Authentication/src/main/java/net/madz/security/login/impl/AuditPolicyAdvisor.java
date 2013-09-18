/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.impl;

import java.util.Properties;

import net.madz.security.login.interfaces.IAuditPolicy;

/**
 * 
 * @author Administrator
 */
public class AuditPolicyAdvisor implements IAuditPolicy {

	public static final String PASSWORD_LIFE_AUDIT_ENABLED = "PasswordLifeAuditEnable";
	public static final String PASSWORD_LIFE_LIMIT = "PasswordLifeLimit";
	public static final String OLD_PASSWORD_TIMES = "OldPasswordTimes";
	public static final String PASSWORD_MIN_LENGTH_ENABLED = "PasswordMinLengthEnabled";
	public static final String PASSWORD_MIN_LENGTH = "PasswordMinLength";
	public static final String PASSWORD_MAX_LENGTH_ENABLED = "PasswordMaxLengthEnabled";
	public static final String PASSWORD_MAX_LENGTH = "PasswordMaxLength";
	public static final String SPECIAL_CHARACTERS_MUST_ENABLED = "SpecialCharactersMustEnabled";
	public static final String SPECIAL_CHARACTERS = "SpecialCharacters";
	public static final String DIGITS_MUST_ENABLED = "DigitsMustEnabled";
	public static final String DIGITS = "Digits";
	public static final String COMMON_CHARACTERS_MUST_ENABLED = "CommonCharactersMustEnabled";
	public static final String COMMON_CHARACTERS = "CommonCharacters";
	public static final String UNLOCK_ENABLED = "UnlockEnabled";
	public static final String UNLOCK_INTERVAL = "UnlockInterval";
	public static final String LOCK_ENABLED = "LockEnabled";
	public static final String PASSWORD_ATTEMPT_LIMIT = "PasswordAttemptLimit";
	public static final String AUDIT_COMPANY_ENABLED = "AuditCompany";
	private static volatile AuditPolicyAdvisor instance;
	private static volatile Properties prop = new Properties();

	public static void setConfigurations(Properties configuration) {
		synchronized (prop) {
			prop.clear();
			prop.putAll(configuration);
		}
	}

	public static AuditPolicyAdvisor getInstance() {
		if (instance == null) {
			synchronized (AuditPolicyAdvisor.class) {
				if (instance == null) {
					instance = new AuditPolicyAdvisor();
				}
			}
		}
		return instance;
	}

	private AuditPolicyAdvisor() {
		// init();
	}

	public void reload() {
		if (prop != null) {
			prop.clear();
			prop = null;
		}
		// init();
	}

	public boolean isPasswordLifeAuditEnabled() {
		if (prop.containsKey(PASSWORD_LIFE_AUDIT_ENABLED)) {
			String result = prop.getProperty(PASSWORD_LIFE_AUDIT_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public long getPasswordLifeLimit() {

		if (prop.containsKey(PASSWORD_LIFE_LIMIT)) {
			String result = prop.getProperty(PASSWORD_LIFE_LIMIT);
			if (result == null || result.trim().length() <= 0) {
				return -1;
			} else {
				return Long.valueOf(result);
			}
		} else {
			return -1;
		}
	}

	public int getOldPasswordTimes() {

		if (prop.containsKey(OLD_PASSWORD_TIMES)) {
			String result = prop.getProperty(OLD_PASSWORD_TIMES);
			if (result == null || result.trim().length() <= 0) {
				return 0;
			} else {
				return Integer.valueOf(result);
			}
		} else {
			return 0;
		}

	}

	public boolean isPasswordMinLengthEnabled() {

		if (prop.containsKey(PASSWORD_MIN_LENGTH_ENABLED)) {
			String result = prop.getProperty(PASSWORD_MIN_LENGTH_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public int getPasswordMinLength() {

		if (prop.containsKey(PASSWORD_MIN_LENGTH)) {
			String result = prop.getProperty(PASSWORD_MIN_LENGTH);
			if (result == null || result.trim().length() <= 0) {
				return 0;
			} else {
				return Integer.valueOf(result);
			}
		} else {
			return 0;
		}

	}

	public boolean isPasswordMaxLengthEnabled() {

		if (prop.containsKey(PASSWORD_MAX_LENGTH_ENABLED)) {
			String result = prop.getProperty(PASSWORD_MAX_LENGTH_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public int getPasswordMaxLength() {

		if (prop.containsKey(PASSWORD_MAX_LENGTH)) {
			String result = prop.getProperty(PASSWORD_MAX_LENGTH);
			if (result == null || result.trim().length() <= 0) {
				return -1;
			} else {
				return Integer.valueOf(result);
			}
		} else {
			return -1;
		}

	}

	public boolean isSpecialCharactersMustEnabled() {

		if (prop.containsKey(SPECIAL_CHARACTERS_MUST_ENABLED)) {
			String result = prop.getProperty(SPECIAL_CHARACTERS_MUST_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public char[] getSpecialCharacters() {
		char[] defaultChar = { '~', '!', '@', '$', '&' };

		if (prop.containsKey(SPECIAL_CHARACTERS)) {
			String result = prop.getProperty(SPECIAL_CHARACTERS);
			String[] chars = result.split(",");
			char[] newChars = new char[chars.length];
			for (int i = 0; i < chars.length; i++) {
				if (chars[i].length() != 1) {
					return defaultChar;
				} else {
					newChars[i] = chars[i].charAt(0);
				}
			}
			return newChars;
		} else {
			return defaultChar;
		}
	}

	public boolean isDigitsMustEnabled() {

		if (prop.containsKey(DIGITS_MUST_ENABLED)) {
			String result = prop.getProperty(DIGITS_MUST_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public int[] getDigits() {
		int[] defaultDigit = { 1, 2, 3, 4, 5 };

		if (prop.containsKey(DIGITS)) {
			String result = prop.getProperty(DIGITS);
			String[] chars = result.split(",");
			int[] newDigits = new int[chars.length];
			for (int i = 0; i < chars.length; i++) {
				if (chars[i].length() != 1) {
					return defaultDigit;
				} else {
					newDigits[i] = Integer.valueOf(chars[i].charAt(0));
				}
			}
			return newDigits;
		} else {
			return defaultDigit;
		}
	}

	public boolean isCommonCharactersMustEnabled() {

		if (prop.containsKey(COMMON_CHARACTERS_MUST_ENABLED)) {
			String result = prop.getProperty(COMMON_CHARACTERS_MUST_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public char[] getCommonCharacters() {
		char[] defaultChar = { 'a', 'b', 'c', 'd', 'e' };

		if (prop.containsKey(COMMON_CHARACTERS)) {
			String result = prop.getProperty(COMMON_CHARACTERS);
			String[] chars = result.split(",");
			char[] newChars = new char[chars.length];
			for (int i = 0; i < chars.length; i++) {
				if (chars[i].length() != 1) {
					return defaultChar;
				} else {
					newChars[i] = chars[i].charAt(0);
				}
			}
			return newChars;
		} else {
			return defaultChar;
		}
	}

	public boolean isAutoUnlockEnabled() {

		if (prop.containsKey(UNLOCK_ENABLED)) {
			String result = prop.getProperty(UNLOCK_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public long getUnlockInterval() {

		if (prop.containsKey(UNLOCK_INTERVAL)) {
			String result = prop.getProperty(UNLOCK_INTERVAL);
			if (result == null || result.trim().length() <= 0) {
				return 0;
			} else {
				return Integer.valueOf(result);
			}
		} else {
			return 0;
		}
	}

	public boolean isLockEnabled() {

		if (prop.containsKey(LOCK_ENABLED)) {
			String result = prop.getProperty(LOCK_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

	public int getPasswordAttemptLimit() {

		if (prop.containsKey(PASSWORD_ATTEMPT_LIMIT)) {
			String result = prop.getProperty(PASSWORD_ATTEMPT_LIMIT);
			if (result == null || result.trim().length() <= 0) {
				return 65535;
			} else {
				return Integer.valueOf(result);
			}
		} else {
			return 65535;
		}
	}

	public boolean isAuditCompanyEnabled() {
		if (prop.containsKey(AUDIT_COMPANY_ENABLED)) {
			String result = prop.getProperty(AUDIT_COMPANY_ENABLED);
			if (result == null || result.trim().length() <= 0) {
				return false;
			} else {
				return result.equalsIgnoreCase("true");
			}
		} else {
			return false;
		}
	}

}
