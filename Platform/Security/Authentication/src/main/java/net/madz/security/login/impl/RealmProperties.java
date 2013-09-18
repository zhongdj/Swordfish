/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.security.login.impl;

import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author Administrator
 */
public class RealmProperties {

	private static volatile Properties prop = new Properties();

	public static void setConfigurations(Properties configuration) {
		synchronized (prop) {
			prop.clear();
			prop.putAll(configuration);
		}
	}

	public static Properties getProperties() throws IOException {
		return (Properties) prop.clone();
	}

	private RealmProperties() {
	}

	public static void reload() {
	}

}
