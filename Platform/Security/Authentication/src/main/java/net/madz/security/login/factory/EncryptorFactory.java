/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.factory;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.security.login.code.Base64;
import net.madz.security.login.code.DefaultEncryptor;
import net.madz.security.login.code.MD5;
import net.madz.security.login.impl.RealmProperties;
import net.madz.security.login.interfaces.IPasswordEncryptor;

/**
 * 
 * @author Administrator
 */
public class EncryptorFactory {

	public static final String ENCRYPTOR = "encryptor";
	private volatile static EncryptorFactory instance;
	private volatile Properties prop;

	public static EncryptorFactory getInstance() {
		if (instance == null) {
			synchronized (EncryptorFactory.class) {
				if (instance == null) {
					instance = new EncryptorFactory();
				}
			}
		}
		return instance;
	}

	private EncryptorFactory() {
	}

	public IPasswordEncryptor getPasswordEncryptor() {

		String name;
		try {
			if (prop == null) {
				prop = RealmProperties.getProperties();
			}
		} catch (IOException ex) {
			Logger.getLogger(EncryptorFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (prop.containsKey(ENCRYPTOR)) {
			name = prop.getProperty(ENCRYPTOR);
		} else {
			name = "none";
		}
		if (name.trim().length() <= 0) {
			name = "none";
		}
		IPasswordEncryptor passwordEncryptor = null;
		if (name.equalsIgnoreCase("MD5")) {
			passwordEncryptor = new MD5();
		} else if (name.equalsIgnoreCase("Base64")) {
			passwordEncryptor = new Base64();
		} else if (name.equalsIgnoreCase("none")) {
			passwordEncryptor = new DefaultEncryptor();
		}
		return passwordEncryptor;
	}

	public void resetProperties(Properties properties) {
		prop = properties;
	}
}
