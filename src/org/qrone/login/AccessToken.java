package org.qrone.login;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.qrone.util.Digest;
import org.qrone.util.Hex;

public class AccessToken {
	
	public static final String MASTER   = "X";
	public static final String GRANT    = "G";
	public static final String PAY      = "P";
	public static final String WRITEC   = "D";
	public static final String WRITE    = "W";
	public static final String READC    = "C";
	public static final String READ     = "R";
	public static final String ADD      = "A";
	public static final String DEVICE   = "D";
	public static final String BROWSER  = "B";
	
	//   Token-[Scope1][:[Scope2][:[Scope3]...]]
	//   .ID-[userID]
	//   [.asID-[asID]]
	//   [.Sign-[Timestamp]:[Sign]]
			
	private String id;
	private String asid;
	private Set<String> scopes;
	private long timestamp;
	private byte[] sign;

	private AccessToken(){
	}

	public AccessToken(String id, String scope){
		this(id, null, scopeSet(scope));
	}
	
	public AccessToken(String id, Set<String> scopes){
		this(id, null, scopes);
	}

	public AccessToken(String id, String asid, String scope){
		this(id, asid, scopeSet(scope));
	}
	
	public AccessToken(String id, String asid, Set<String> scopes){
		this.id = id;
		this.asid = asid;
		this.scopes = scopes;
	}	
		
	public void sign(UUID signersecret){
		this.timestamp = System.currentTimeMillis();
		this.sign = calcSign(signersecret.toString());
	}

	public boolean validate(UUID signersecret, String scope, long millis){
		if(validate(signersecret, scope) && (System.currentTimeMillis() - timestamp) < millis){
			return true;
		}
		return false;
	}
	
	public boolean validate(UUID signersecret, String scope){
		if(validate(signersecret)){
			if(scopes.contains(scope) || scopes.contains("X")){
				return true;
			}
			if(scope.equals("R") && scopes.contains("C")){
				return true;
			}
			if(scope.equals("W") && scopes.contains("D")){
				return true;
			}
		}
		return false;
	}
	
	public boolean validate(UUID signersecret){
		return Arrays.equals(sign, calcSign(signersecret.toString()));
	}
	
	private byte[] calcSign(String secret){
		try {
			return Digest.digest("SHA1", (toTokenString(secret)).getBytes());
		} catch (NoSuchAlgorithmException e) {}
		return new byte[0];
	}
	
	public static AccessToken parse(String tokenString){
		if(tokenString == null) return null;
		
		AccessToken t = new AccessToken();
		try{
			String[] s = tokenString.split("\\.");
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < s.length; i++) {
				map.put(s[i].substring(0, s[i].indexOf("-")), s[i]);
			}
			

			String token = map.get("Token");
			if(token != null){
				String[] sc = token.substring("Token-".length()).split("\\:");
				t.scopes = new HashSet<String>();
				for (int i = 0; i < sc.length; i++) {
					t.scopes.add(Hex.hex2str(sc[i]));
				}
			}
			
			String id = map.get("ID");
			if(token != null){
				t.id = id;
			}
			
			String asid = map.get("asID");
			if(asid != null){
				t.asid = asid.substring(2);
			}
			
			String sign = map.get("Sign");
			if(sign != null){
				String[] ss = sign.substring("Sign-".length()).split("\\:");
				
				t.timestamp = Hex.hex2long(ss[0]);
				t.sign = Hex.hex2bytearray(ss[1]);
			}
			
			return t;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static Set<String> scopeSet(String scope){
		Set<String> set = new HashSet<String>();
		set.add(scope);
		return set;
	}
	
	public static String uniqueid(){
		return Hex.long2hex(System.currentTimeMillis())
				+ Hex.double2hex(Math.random());
	}
	
	public String getId(){
		return id;
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	private String toBodyString(){
		StringBuilder sb = new StringBuilder();

		sb.append("Token-");
		
		boolean first = true;
		if(scopes != null){
			for (Iterator<String> iter = scopes.iterator(); iter.hasNext();) {
				if(!first){
					sb.append(":");
				}
				sb.append(Hex.str2hex(iter.next()));
				first = false;
			}
		}
		
		if(id != null){
			sb.append(".");
			sb.append(id.toString());
		}
		
		if(asid != null){
			sb.append(".as");
			sb.append(asid.toString());
		}
		
		return sb.toString();
	}
	
	private String toTokenString(String signstr){
		StringBuilder sb = new StringBuilder();
		sb.append(toBodyString());
		
		sb.append(".Sign-");
		sb.append(Hex.long2hex(timestamp));

		sb.append(":");
		sb.append(signstr);
		
		return sb.toString();
	}

	public String toString(){
		if(sign != null){
			return toTokenString(Hex.bytearray2hex(sign));
		}
		return toBodyString();
	}
	
	public static void main(String[] args){
		
		AccessToken t1 = ID.generateAccessToken(UUID.randomUUID(), "SCOPE");
		System.out.println(t1.toString());
		
		AccessToken t11 = AccessToken.parse(t1.toString());
		System.out.println(t11.toString());
		
		AccessToken t2 = ID.generateAccessToken(UUID.randomUUID(), "SCOPE");
		System.out.println(t2.toString());

		AccessToken t22 = AccessToken.parse(t2.toString());
		System.out.println(t22.toString());
		
		UUID secret = UUID.randomUUID();
		
		t1.sign(secret);
		System.out.println(t1.toString());
		System.out.println("length: " + t1.toString().length());
		
		
		AccessToken t3 = AccessToken.parse(t1.toString());
		System.out.println(t3.toString());
		System.out.println(t3.validate(secret));
		System.out.println(t3.validate(secret, "SCOPE"));
		System.out.println(t3.validate(secret, "SCOPE", 1000));
		
	}

}
