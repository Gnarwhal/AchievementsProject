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

	public int getUser(String key) {
		return session.get(key);
	}

	public void remove(String key) {
		session.remove(key);
	}
}
