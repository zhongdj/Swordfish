/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import net.madz.security.login.exception.UserNotExistException;
import net.madz.security.login.factory.BadRealmException;
import net.madz.security.login.factory.EncryptorFactory;
import net.madz.security.login.interfaces.ICompany;
import net.madz.security.login.interfaces.IPrincipal;

/**
 * 
 * @author Administrator
 */
public class Principal implements IPrincipal {

	// User Table Relative
	public static final String USER_TABLE = "user_table";
	public static final String USERNAME_COL = "username_col";
	public static final String ENCRYPTEDPASSWORD_COL = "encryptedPassword_col";
	public static final String INVALID_PASSWORD_ATTEMPTS_COL = "invalidPasswordAttempts_col";
	public static final String LOCKED_COL = "locked_col";
	public static final String LAST_FAILED_TIME = "last_failed_time_col";
	public static final String OLD_PASSWORDS_COL = "oldPasswords_col";
	public static final String LAST_PWD_CHANGE_TIME_COL = "last_pwd_change_time_col"; // added
	// by
	// Barry
	public static final String FROZEN_FLAG_COL = "frozen_flag_col";
	public static final String NEED_RESET_PWD_COL = "need_reset_pwd_col";
	public static final String LOGIN_DATE_COL = "login_date_col";
	public static final String LAST_LOGIN_DATE_COL = "last_login_date_col";
	public static final String LOGIN_TIMES_COL = "login_times_col";
	private static final String ACCESS_DENIED_TIMES_COL = "access_denied_times_col";
	private final String UPDATE_ACCOUNT_QL; // company Table Relative
	public static final String USER_COMPANY_FK = "user_company_fk";
	public static final String COMPANY_TABLE = "company_table";
	public static final String COMPANY_ID_COL = "company_id_col";
	public static final String COMPANY_NAME_COL = "company_name_col";
	public static final String COMPANY_LOCK_COL = "company_lock_col";
	public static final String COMPANY_FROZEN_COL = "company_frozen_col";
	private final String SELECT_COMPANY_QL; // group Table Relative
	public static final String GROUP_TABLE = "group_table";
	public static final String GROUP_JOIN_COL = "group_join_col";
	public static final String USER_JOIN_COL = "user_join_col";
	public static final String USER_GROUP_JOIN_TABLE = "user_group_join_table";
	public static final String GROUP_NAME_COL = "group_name_col";
	private final String SELECT_GROUP_QL; // configuration properties
	private static volatile Properties prop; // principal biz state
	private boolean frozen;
	private boolean needResetpwd;
	private Timestamp loginDate;
	private Timestamp lastFailedTime;
	private Timestamp lastChangepwdTime;
	private Timestamp lastLoginDate;
	private int loginTimes;
	private String password;
	private boolean locked;
	private int invalidPasswordAttempts;
	private String name;
	private int accessDeniedTimes;
	private double passwordLife;
	private double lockedDays;
	private String encryptedPassword;
	private List<String> oldPasswords;
	private volatile List<String> groups;

	private Principal(String userName, String encryptedPassword) {

		this.name = userName;
		this.encryptedPassword = encryptedPassword;

		{
			String userTable = prop.getProperty(USER_TABLE, "ACCOUNT");
			String usernameCol = prop.getProperty(USERNAME_COL, "ACCOUNTNAME");
			String passwordCol = prop.getProperty(ENCRYPTEDPASSWORD_COL, "PASSWORD");
			String invalidPwdAttemptsCol = prop.getProperty(INVALID_PASSWORD_ATTEMPTS_COL, "");
			String lockCol = prop.getProperty(LOCKED_COL, "LOCKFLAG");
			String lastFailedTimeCol = prop.getProperty(LAST_FAILED_TIME, "LASTFAILEDTIME");
			String oldPasswordsCol = prop.getProperty(OLD_PASSWORDS_COL, "OLDPASSWORDS");
			String lastPwdChangeTimeCol = prop.getProperty(LAST_PWD_CHANGE_TIME_COL, "LASTCHANGEPWDTIME");
			String freezeCol = prop.getProperty(FROZEN_FLAG_COL, "FREEZENFLAG");
			String needResetPwdCol = prop.getProperty(NEED_RESET_PWD_COL, "NEEDRESETPWD");
			String loginDateCol = prop.getProperty(LOGIN_DATE_COL, "LOGINDATE");
			String lastLoginDateCol = prop.getProperty(LAST_LOGIN_DATE_COL, "LASTLOGINDATE");
			String loginTimesCol = prop.getProperty(LOGIN_TIMES_COL, "LOGINTIMES");
			String accessDeniedTimesCol = prop.getProperty(ACCESS_DENIED_TIMES_COL, "ACCESSDENIEDTIMES");

			UPDATE_ACCOUNT_QL = "UPDATE " + userTable + " SET " + freezeCol + "= ?," + needResetPwdCol + " =?," + loginDateCol + " =?,"
					+ lockCol + " =?," + lastLoginDateCol + "=?," + invalidPwdAttemptsCol + " =?," + lastFailedTimeCol + "=?,"
					+ lastPwdChangeTimeCol + " =?," + loginTimesCol + "=?," + accessDeniedTimesCol + "=?," + passwordCol + "=?, "
					+ oldPasswordsCol + "=? WHERE " + usernameCol + "=?";

			if (AuditPolicyAdvisor.getInstance().isAuditCompanyEnabled()) {

				String fk = prop.getProperty(USER_COMPANY_FK, "COMPANY_ID");
				String companyTable = prop.getProperty(COMPANY_TABLE, "COMPANY");
				String companyIdCol = prop.getProperty(COMPANY_ID_COL, "ID");

				SELECT_COMPANY_QL = "SELECT c.* FROM " + companyTable + " c, " + userTable + " u   WHERE u." + fk + " = c." + companyIdCol
						+ " AND u." + usernameCol + " = ?";
			} else {
				SELECT_COMPANY_QL = "";
			}

			String groupTable = prop.getProperty(GROUP_TABLE, "USERGROUP");
			String groupJoinTable = prop.getProperty(USER_GROUP_JOIN_TABLE, "USER_GROUP");
			String userJoinCol = prop.getProperty(USER_JOIN_COL, "USER_NAME");
			String groupJoinCol = prop.getProperty(GROUP_JOIN_COL, "GROUP_NAME");
			String groupNameCol = prop.getProperty(GROUP_NAME_COL, "NAME");

			SELECT_GROUP_QL = "SELECT g.* FROM " + groupTable + " g, " + groupJoinTable + " j, " + userTable + " u " + " WHERE u."
					+ usernameCol + " = j." + userJoinCol + " AND j." + groupJoinCol + " = g." + groupNameCol + " AND u." + usernameCol
					+ " = ?";
		}

	}

	public static IPrincipal findPrincipal(String userName) throws UserNotExistException {
		try {
			init();
		} catch (IOException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		}
		Principal principal = null;
		Connection conn = null;
		PreparedStatement ps = null;
		if (!prop.containsKey(USER_TABLE)) {
			throw new BadRealmException(USER_TABLE + " property not found.");
		}

		String tableName = prop.getProperty(USER_TABLE);
		if (tableName.trim().length() <= 0) {
			throw new BadRealmException(USER_TABLE + " property is empty.");
		}
		if (!prop.containsKey(USERNAME_COL)) {
			throw new BadRealmException(USERNAME_COL + " property not found.");
		}
		String userNameCol = prop.getProperty(USERNAME_COL);
		if (userNameCol.trim().length() <= 0) {
			throw new BadRealmException(USERNAME_COL + " property is empty.");
		}

		try {
			conn = DBHelper.getInstance().getConnection();
			ps = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE " + userNameCol + " = ?");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (prop.containsKey(ENCRYPTEDPASSWORD_COL)) {
					String encryptedPassword_col = prop.getProperty(ENCRYPTEDPASSWORD_COL);
					if (encryptedPassword_col.trim().length() > 0) {
						String pwd = rs.getString(encryptedPassword_col);
						if (pwd != null && pwd.trim().length() > 0) {
							principal = new Principal(userName, pwd);
						} else {
							principal = new Principal(userName, "");
						}
					} else {
						throw new BadRealmException(ENCRYPTEDPASSWORD_COL + " property is empty.");
					}
				} else {
					throw new BadRealmException(ENCRYPTEDPASSWORD_COL + " property not found.");
				}

				if (prop.containsKey(INVALID_PASSWORD_ATTEMPTS_COL)) {
					String invalidPasswordAttempts_col = prop.getProperty(INVALID_PASSWORD_ATTEMPTS_COL);
					if (invalidPasswordAttempts_col.trim().length() > 0) {
						int invalidPwdAttemps = rs.getInt(invalidPasswordAttempts_col);
						principal.invalidPasswordAttempts = invalidPwdAttemps;
					} else {
						principal.invalidPasswordAttempts = 0;
					}
				} else {
					principal.invalidPasswordAttempts = 0;
				}

				if (prop.containsKey(LAST_FAILED_TIME)) {
					String lockedDays_col = prop.getProperty(LAST_FAILED_TIME);
					if (lockedDays_col.trim().length() > 0) {
						Timestamp lockedDaysTimestamp = rs.getTimestamp(lockedDays_col);
						if (lockedDaysTimestamp == null) {
							principal.lockedDays = 0d;
						} else {
							double lockedTime = lockedDaysTimestamp.getTime();
							double currentTime = System.currentTimeMillis();
							double lockedDay = (currentTime - lockedTime) / (3600 * 24 * 1000);
							if (lockedDay < 0) {
								lockedDay = 0;
							}
							principal.lockedDays = lockedDay;
						}
					} else {
						principal.lockedDays = 0;
					}
				} else {
					principal.lockedDays = 0;
				}

				if (prop.containsKey(LOCKED_COL)) {
					String locked_col = prop.getProperty(LOCKED_COL);
					if (locked_col.trim().length() > 0) {
						principal.locked = rs.getBoolean(locked_col);
					} else {
						principal.unlock();
					}
				} else {
					principal.unlock();
				}

				if (prop.containsKey(OLD_PASSWORDS_COL)) {
					String oldPasswords_col = prop.getProperty(OLD_PASSWORDS_COL);

					if (oldPasswords_col.trim().length() > 0) {
						String oldPasswordsString = rs.getString(oldPasswords_col);
						if (oldPasswordsString != null) {
							String[] oldPasswordsArray = oldPasswordsString.split(",");
							List<String> oldPasswordArrayList = new LinkedList<String>();
							if (oldPasswordsArray.length > 0) {
								for (int i = 0; i < oldPasswordsArray.length; i++) {
									oldPasswordArrayList.add(oldPasswordsArray[i]);
								}
								principal.oldPasswords = oldPasswordArrayList;
							}
						}
					}

					if (principal.oldPasswords == null) {
						principal.oldPasswords = new LinkedList<String>();
					}
				} else {
					principal.oldPasswords = new LinkedList<String>();
				}

				if (prop.containsKey(LAST_PWD_CHANGE_TIME_COL)) {
					String passwordLife_col = prop.getProperty(LAST_PWD_CHANGE_TIME_COL);
					if (passwordLife_col.trim().length() > 0) {
						double lastChangedTime = rs.getTimestamp(passwordLife_col).getTime();
						double currentTime = System.currentTimeMillis();
						double life = (currentTime - lastChangedTime) / (24 * 3600 * 1000);
						principal.passwordLife = life;
					} else {
						principal.passwordLife = 0;
					}
				} else {
					principal.passwordLife = 0;
				}
				principal.setFrozen(rs.getBoolean("FREEZENFLAG"));
				principal.needResetpwd = rs.getBoolean("NEEDRESETPWD");
				if (rs.getTimestamp("LOGINDATE") == null) {
					principal.loginDate = (new Timestamp(System.currentTimeMillis()));
				} else {
					principal.loginDate = rs.getTimestamp("LOGINDATE");
				}
				if (rs.getTimestamp("LASTFAILEDTIME") == null) {
					principal.lastFailedTime = new Timestamp(System.currentTimeMillis());
				} else {
					principal.lastFailedTime = rs.getTimestamp("LASTFAILEDTIME");
				}
				if (rs.getTimestamp("LASTCHANGEPWDTIME") == null) {
					principal.lastChangepwdTime = new Timestamp(System.currentTimeMillis());
				} else {
					principal.lastChangepwdTime = rs.getTimestamp("LASTCHANGEPWDTIME");
				}
				if (rs.getTimestamp("LASTLOGINDATE") == null) {
					principal.lastLoginDate = new Timestamp(System.currentTimeMillis());
				} else {
					principal.lastLoginDate = rs.getTimestamp("LASTLOGINDATE");
				}
				principal.loginTimes = rs.getInt("LOGINTIMES");
			}

		} catch (NamingException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				ps.close();
			} catch (Exception ex) {
				Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
			}
			try {
				conn.close();
			} catch (Exception ex) {
				Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return principal;
	}

	public ICompany findCompany() {
		if (SELECT_COMPANY_QL.length() <= 0) {
			return null;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		Company company = null;
		try {
			conn = DBHelper.getInstance().getConnection();
			ps = conn.prepareStatement(SELECT_COMPANY_QL);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String companyNameCol = prop.getProperty(COMPANY_NAME_COL, "NAME");
				String companyLockCol = prop.getProperty(COMPANY_LOCK_COL, "LOCKED");
				String companyFreezeCol = prop.getProperty(COMPANY_FROZEN_COL, "FREEZEN");
				String companyName = rs.getString(companyNameCol);
				int lockedIndicator = rs.getInt(companyLockCol);
				int freezen = rs.getInt(companyFreezeCol);
				if (companyName == null) {
					companyName = "";
				}
				company = new Company(companyName, lockedIndicator > 0 ? true : false, freezen > 0 ? true : false);
			}

			return company;
		} catch (NamingException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				ps.close();
			} catch (Exception ex) {
				Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
			}
			try {
				conn.close();
			} catch (Exception ex) {
				Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return company;
	}

	public String[] findGroup() {
		if (SELECT_GROUP_QL.length() <= 0) {
			return null;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBHelper.getInstance().getConnection();
			ps = conn.prepareStatement(SELECT_GROUP_QL);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String groupName = rs.getString(prop.getProperty(GROUP_NAME_COL, "NAME"));
				addGroup(groupName);
			}
		} catch (NamingException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				ps.close();
			} catch (Exception ex) {
				Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
			}
			try {
				conn.close();
			} catch (Exception ex) {
				Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		if (groups != null) {
			String[] result = new String[groups.size()];
			for (int i = 0; i < groups.size(); i++) {
				result[i] = groups.get(i);
			}
			return result;
		} else {
			return new String[0];
		}
	}

	public static void resetProperties(Properties properties) {
		prop = properties;
	}

	private static void init() throws IOException {
		prop = RealmProperties.getProperties();
	}

	public void handleLoginSuccess() {
		this.invalidPasswordAttempts = 0;
		this.loginTimes++;
		this.lastLoginDate = this.loginDate;
		this.loginDate = new Timestamp(System.currentTimeMillis());
	}

	public void handleLoginFailed() {
		this.invalidPasswordAttempts++;
		this.lastFailedTime = new Timestamp(System.currentTimeMillis());
	}

	public int getAccessDeniedTimes() {
		return accessDeniedTimes;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public Timestamp getLastChangepwdTime() {
		return lastChangepwdTime;
	}

	public Timestamp getLastFailedTime() {
		return lastFailedTime;
	}

	public Timestamp getLastLoginDate() {
		return lastLoginDate;
	}

	public Timestamp getLoginDate() {
		return loginDate;
	}

	public int getLoginTimes() {
		return loginTimes;
	}

	public boolean isNeedResetpwd() {
		return needResetpwd;
	}

	public String getPassword() {
		return password;
	}

	public double getLockedMills() {
		return lockedDays;
	}

	public String getName() {
		return name;
	}

	public double getPasswordLife() {
		return passwordLife;
	}

	public List<String> getOldPasswords() {
		return oldPasswords;
	}

	public String getOldPasswordStr() {
		StringBuffer result = new StringBuffer();
		if (oldPasswords == null || oldPasswords.size() == 0) {
			return "";
		} else {
			int size = oldPasswords.size();
			int count = 1;
			for (String pwd : oldPasswords) {

				result.append(pwd);
				if (count < size) {
					result.append(",");
					count++;
				}
			}
			return result.toString();
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public double getlockedDays() {
		return lockedDays;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
		invalidPasswordAttempts = 0;
	}

	private void addGroup(String group) {
		if (groups == null) {
			synchronized (Principal.class) {
				if (groups == null) {
					groups = new LinkedList<String>();
				}
			}
		}
		groups.add(group);
	}

	private void addOldPassword() {
		if (oldPasswords.size() >= AuditPolicyAdvisor.getInstance().getOldPasswordTimes()) {
			oldPasswords.remove(0);
		}
		oldPasswords.add(encryptedPassword);
	}

	public int getInvalidPasswordAttempts() {
		return invalidPasswordAttempts;
	}

	public void resetPassword(String oldPassword, String newPassword, String confirmPassword) {
		if (EncryptorFactory.getInstance().getPasswordEncryptor().encrypt(oldPassword).equals(encryptedPassword)) {
			if (newPassword.equals(confirmPassword)) {
				addOldPassword();
				lastChangepwdTime = new Timestamp(System.currentTimeMillis());
				encryptedPassword = EncryptorFactory.getInstance().getPasswordEncryptor().encrypt(confirmPassword);
			}
		}
	}

	public void save() {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = DBHelper.getInstance().getConnection();
			ps = conn.prepareStatement(UPDATE_ACCOUNT_QL);
			ps.setBoolean(1, frozen);
			ps.setBoolean(2, needResetpwd);
			ps.setTimestamp(3, loginDate);
			ps.setBoolean(4, locked);
			ps.setTimestamp(5, lastLoginDate);
			ps.setInt(6, invalidPasswordAttempts);
			ps.setTimestamp(7, lastFailedTime);
			ps.setTimestamp(8, lastChangepwdTime);
			ps.setInt(9, loginTimes);
			ps.setInt(10, accessDeniedTimes);
			ps.setString(11, encryptedPassword);
			ps.setString(12, getOldPasswordStr());
			ps.setString(13, name);
			ps.execute();
		} catch (NamingException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				conn.close();
				ps.close();
			} catch (Exception ex) {
				Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer oldPasswordString = new StringBuffer();
		if (oldPasswords == null || oldPasswords.size() <= 0) {
		} else {
			for (String pwd : oldPasswords) {
				oldPasswordString.append(",");
				oldPasswordString.append(pwd);
			}
			if (oldPasswordString.length() > 1) {
				oldPasswordString.deleteCharAt(0);
			}
		}
		return getClass().getName() + ":[ name: " + name + ", encryptedPassword: " + encryptedPassword + ", invalidPasswordAttempts:"
				+ invalidPasswordAttempts + ", passwordLife:" + passwordLife + ", locked: " + locked + ", lockedMills: " + lockedDays
				+ ", oldPasswords:" + oldPasswordString;
	}
}
