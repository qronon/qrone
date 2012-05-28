package org.qrone.messaging;

import java.util.Map;

public interface MessagingService {
	
	public void to(String target, Map obj);
	
	public void addListener(Listener l);
	public void removeListener(Listener l);
	
	public interface Listener{
		public void onData(String target, Map obj);
		public void onJoin(MessagingClientConn conn, String target);
		public void onLeft(MessagingClientConn conn, String target);
	}
}
