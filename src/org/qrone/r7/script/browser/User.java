package org.qrone.r7.script.browser;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.qrone.r7.QrONEUtils;

public class User implements Serializable, Externalizable{
	private String login = null;
	private String key = null;
	
	public User(String login, String key) {
		this.login = login;
		if(this.login == null)
			this.login = "guest";
		this.key = key;
	}
	
	public static User createUser(String qcookie){
		String[] qs = qcookie.split(":", 2);
		User user = new User(
				new String(QrONEUtils.decodeQ64(qs[0])), 
				new String(QrONEUtils.decodeQ64(qs[1]))
			);
		return user;
	}
	
	public String getQCookie(){
		return QrONEUtils.encodeQ64(login.getBytes()) 
			+ ":" + QrONEUtils.encodeQ64(key.getBytes());
	}
	
	public String getKey() {
		return key;
	}

	public String getLogin() {
		return login;
	}
	
	public boolean isAdmin() {
		return false;
	}
	
	@Override
	public String toString() {
		return getQCookie();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		login = in.readUTF();
		key = in.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(login);
		out.writeUTF(key);
	}

}
