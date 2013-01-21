package org.qrone.login;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.qrone.kvs.KeyValueStore;
import org.qrone.kvs.KeyValueStoreService;
import org.qrone.r7.handler.URIHandler;
import org.qrone.r7.script.browser.User;
import org.qrone.util.QrONEUtils;
import org.qrone.util.QueryString;
import org.qrone.util.Serialization;

public class LoginHandler implements URIHandler, LoginService{
	private ConsumerManager manager;
	private KeyValueStore store;
	
	public LoginHandler(KeyValueStoreService service) {
		this.store = service.getKeyValueStore("qrone.openid");
	}
	
	
	@Override
	public boolean handle(HttpServletRequest request, HttpServletResponse response, 
			String uri, String path, String pathArg) {
		
		if(path.equals("/system/openid/login")){
			handleOpenIDLogin(request, response);
		}else if(path.equals("/system/openid/verify")){
			handleOpenIDVerify(request, response);
		}else if(path.equals("/system/logout")){
			handleLogout(request, response);
		}
		
		return false;
	}

	public String getOpenIDLoginURL(String url, Map attrMap, String doneURL){
		LoginPack pack = new LoginPack();
		pack.url = url;
		pack.attributes = attrMap;
		return "/system/openid/login?pack=" 
			+ QrONEUtils.packEQ64(pack) + "&.done=" + QrONEUtils.escape(doneURL);
	}

	public String getLoginURL(String doneURL){
		return "/system/login?.done=" + QrONEUtils.escape(doneURL);
	}
	
	public String getLogoutURL(String doneURL){
		return "/system/logout?.done=" + QrONEUtils.escape(doneURL);
	}
	
	private boolean handleLogout(HttpServletRequest req, HttpServletResponse res){
		User user = (User)req.getAttribute("User");
		user.logout();
		return false;
	}
	
	/*
	public String openid_login_url(String url, Scriptable attributes, String doneURL){
		Map<String, String> attrMap = new HashMap<String, String>();
		if(attributes != null){
			Object[] ids = attributes.getIds();
			for (int i = 0; i < ids.length; i++) {
				if(ids[i] instanceof String){
					Object v = attributes.get((String)ids[i], attributes);
					if(v instanceof String){
						attrMap.put((String)ids[i], (String)v);
					}
				}
			}
		}
		return loginURL(url, attrMap, doneURL);
	}
	*/
	
	/*
	private String getBaseURL(HttpServletRequest req){
		int port = req.getServerPort();
		if(port == 80)
			return "http://" + req.getServerName() + "/openid";
		else
			return "http://" + req.getServerName() + ":" + port + "/openid";
	}
	*/
	private boolean handleOpenIDLogin(HttpServletRequest req, HttpServletResponse res){
		try{
			User user = (User)req.getAttribute("User");
			
			LoginPack pack = (LoginPack)QrONEUtils.unpackEQ64(LoginPack.class, req.getParameter("pack"));
			String url = pack.url;
			Map<String, String> attributes = pack.attributes;
			
			if(manager == null){
				manager = new ConsumerManager();
			}
			
			URI reqURL = new URI(req.getRequestURL().toString()).resolve("/system/openid");
			
			List discoveries = manager.discover(url);
			DiscoveryInformation discovered = manager.associate(discoveries);
			AuthRequest authReq = manager.authenticate(discovered, 
					reqURL.toString() + "/verify?.done=" + QrONEUtils.escape(req.getParameter(".done")));
			FetchRequest fetch = FetchRequest.createFetchRequest();
			
			store.set("openid-discover:" + user.getBrowserId(), Serialization.serialize(discovered), true);
			
			for (Iterator<Entry<String, String>> i = attributes.entrySet().iterator(); i
					.hasNext();) {
				Entry<String, String> e = i.next();
				fetch.addAttribute(e.getKey(), e.getValue(), true);
			}
			
			authReq.addExtension(fetch);
			res.sendRedirect(authReq.getDestinationUrl(true));
			return true;
		}catch (OpenIDException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}
	
    // --- processing the authentication response ---
    private boolean handleOpenIDVerify(HttpServletRequest req, HttpServletResponse res)
    {
        try{
			User user = (User)req.getAttribute("User");
			
            ParameterList response =
                    new ParameterList(req.getParameterMap());
            DiscoveryInformation discovered = 
            	(DiscoveryInformation)Serialization.unserialize((byte[])store.get("openid-discover:" + user.getBrowserId()));

            StringBuffer receivingURL = req.getRequestURL();
            String queryString = req.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(req.getQueryString());
            VerificationResult verification = manager.verify(
                    receivingURL.toString(),
                    response, discovered);
            Identifier verified = verification.getVerifiedId();
            if (verified != null)
            {
                AuthSuccess authSuccess =
                        (AuthSuccess) verification.getAuthResponse();
                
                String name = null;
                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX))
                {
                    FetchResponse fetchResp = (FetchResponse) authSuccess
                            .getExtension(AxMessage.OPENID_NS_AX);
                    
                    if(name == null)
                    	name = fetchResp.getAttributeValue("login");
                }
                
                user.openidLogin(verified, authSuccess);
                
                res.sendRedirect(req.getParameter(".done"));
                return true;
            }
        }catch (OpenIDException e){
			e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
        return false;
    }
    
    public static class LoginPack implements Serializable, Externalizable{
		private static final long serialVersionUID = 7001446077656573040L;
		public String url;
    	public Map<String, String> attributes;
    	
		@Override
		public void readExternal(ObjectInput in) throws IOException,
				ClassNotFoundException {
			url = in.readUTF();
			attributes = new HashMap<String, String>();
			int c = in.readInt();
			for (int i = 0; i < c; i++) {
				String k = in.readUTF();
				String v = in.readUTF();
				attributes.put(k, v);
			}
		}
		@Override
		public void writeExternal(ObjectOutput out) throws IOException {
			out.writeUTF(url);
			out.writeInt(attributes.size());
			for (Iterator<Entry<String, String>> i = attributes.entrySet().iterator(); i
					.hasNext();) {
				Entry<String, String> e = i.next();
				out.writeUTF(e.getKey());
				out.writeUTF(e.getValue());
			}
		}
    }

}
