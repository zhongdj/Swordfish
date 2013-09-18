/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.appserv.security;

import javax.security.auth.login.LoginException;

import com.sun.appserv.security.AppservPasswordLoginModule;

/**
 * 
 * @author CleaNEr
 */
public class MadzLoginModule extends AppservPasswordLoginModule {

	@SuppressWarnings("deprecation")
	@Override
	protected void authenticateUser() throws LoginException {
		String[] grpList = null;

		if (!(_currentRealm instanceof MadzRealm)) {
			throw new LoginException("Not Madz Realm Used. Please contact administrator.");
		}

		MadzRealm madzRealm = (MadzRealm) _currentRealm;
		grpList = madzRealm.authenticate(_username, _password);

		if (grpList == null || grpList.length <= 0) {
			throw new LoginException("No Group for:[" + _username + "]");
		}
		commitUserAuthentication(grpList);
	}
}
