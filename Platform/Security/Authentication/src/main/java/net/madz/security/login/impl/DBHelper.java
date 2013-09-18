/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * 
 * @author Administrator
 */
public class DBHelper {

	private String datasourceName;
	private String dbuser;
	private String dbpass;
	private static volatile DBHelper instance;
	public static final String DATASOURCE_JNDI = "datasource";
	public static final String DB_USER = "db_user";
	public static final String DB_PASS = "db_pass";

	private DBHelper() {
		try {
			Properties prop = RealmProperties.getProperties();
			datasourceName = prop.getProperty(DATASOURCE_JNDI);
			dbuser = prop.getProperty(DB_USER);
			dbpass = prop.getProperty(DB_PASS);
		} catch (IOException ex) {
			Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static DBHelper getInstance() {
		if (instance == null) {
			synchronized (DBHelper.class) {
				if (instance == null) {
					instance = new DBHelper();
				}
			}
		}
		return instance;
	}

	public Connection getConnection() throws NamingException, SQLException {
		InitialContext ic = new InitialContext();
		DataSource ds = (DataSource) ic.lookup(datasourceName);
		Connection conn;
		if (dbuser == null || dbuser.trim().length() <= 0 || dbpass == null) {
			conn = ds.getConnection();
		} else {
			conn = ds.getConnection(dbuser, dbpass);
		}
		return conn;
	}
}
