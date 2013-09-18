/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.interfaces;

/**
 * 
 * @author Administrator
 */
public interface IAuditPolicy {

	boolean isPasswordLifeAuditEnabled();

	long getPasswordLifeLimit();

	int getOldPasswordTimes();

	boolean isPasswordMinLengthEnabled();

	int getPasswordMinLength();

	boolean isPasswordMaxLengthEnabled();

	int getPasswordMaxLength();

	boolean isSpecialCharactersMustEnabled();

	char[] getSpecialCharacters();

	boolean isDigitsMustEnabled();

	boolean isCommonCharactersMustEnabled();

	char[] getCommonCharacters();

	boolean isAutoUnlockEnabled();

	long getUnlockInterval();

	boolean isLockEnabled();

	int getPasswordAttemptLimit();

	boolean isAuditCompanyEnabled();
}
