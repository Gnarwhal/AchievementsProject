package achievements.misc;

import java.util.HashMap;

public class SessionManager {

	private HashMap<String, Integer> session;

	public SessionManager() {
		session = new HashMap();
	}

	public String generate(Integer user) {
		var key = HashManager.encode(HashManager.generateBytes(16));
		session.put(key, user);
		return key;
	}

	public String guest() {
		var key = HashManager.encode(HashManager.generateBytes(16));
		session.put(key, null);
		return key;
	}

	public Integer getUser(String key) {
		return session.get(key);
	}
}
