package org.qrone.r7.script.browser;

import org.qrone.r7.QrONEUtils;

public class User{
	private String name = null;
	private String key = null;
	
	public User(String name, String key) {
		this.name = name;
		if(this.name == null)
			this.name = "guest";
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
		return QrONEUtils.encodeQ64(name.getBytes()) 
			+ ":" + QrONEUtils.encodeQ64(key.getBytes());
	}
	
	public String getKey() {
		return key;
	}

	public String getLogin() {
		return name;
	}
	
	public boolean isAdmin() {
		return false;
	}

}
