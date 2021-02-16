package achievements.misc;

import achievements.data.Session;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class SessionManager {

	private HashMap<String, Session> sessions;

	public SessionManager() {
		sessions = new HashMap();
	}

	public Session generate(int user, int hue) {
		var key = HashManager.encode(HashManager.generateBytes(16));
		var session = new Session(key, user, hue);
		sessions.put(key, session);
		return session;
	}

	public int getUser(String key) {
		return sessions.get(key).getId();
	}

	public void remove(String key) {
		sessions.remove(key);
	}

	public boolean validate(int user, String key) {
		var foreign = sessions.get(key);
		return foreign != null && user == foreign.getId();
	}

	public boolean refresh(String key) {
		var foreign = sessions.get(key);
		if (foreign != null) {
			foreign.setUsed(true);
			return true;
		} else {
			return false;
		}
	}

	// Clean up inactive sessions
	@Scheduled(cron = "0 */30 * * * *")
	public void clean() {
		var remove = new ArrayList<String>();
		sessions.forEach((key, session) -> {
			if (!session.getUsed()) {
				remove.add(session.getKey());
			} else {
				session.setUsed(false);
			}
		});
		for (var session : remove) {
			sessions.remove(session);
		}
	}
}
