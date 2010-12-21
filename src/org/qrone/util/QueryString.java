package org.qrone.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class QueryString {

	private Map<String, List<String>> parameters;

	public QueryString(String qs) {
		parameters = new TreeMap<String, List<String>>();
		if(qs == null) return;

		// Parse query string
		String pairs[] = qs.split("&");
		for (String pair : pairs) {
			String name;
			String value;
			int pos = pair.indexOf('=');
			// for "n=", the value is "", for "n", the value is null
			if (pos == -1) {
				name = pair;
				value = null;
			} else {
				try {
					name = URLDecoder.decode(pair.substring(0, pos), "UTF-8");
					value = URLDecoder.decode(
							pair.substring(pos + 1, pair.length()), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// Not really possible, throw unchecked
					throw new IllegalStateException("No UTF-8");
				}
			}
			List<String> list = parameters.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				parameters.put(name, list);
			}
			list.add(value);
		}
	}

	public String getParameter(String name) {
		List<String> values = parameters.get(name);
		if (values == null)
			return null;

		if (values.size() == 0)
			return "";

		return values.get(0);
	}

	public List<String> getParameterValues(String name) {
		return parameters.get(name);
	}

	public Set<String> getParameterNames() {
		return parameters.keySet();
	}

	public Map<String, List<String>> getParameterMap() {
		return parameters;
	}
}