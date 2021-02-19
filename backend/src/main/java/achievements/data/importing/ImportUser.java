package achievements.data.importing;

import achievements.data.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportUser extends User {

	@JsonProperty("userId")
	private int userId;
	@JsonProperty("sessionKey")
	private String sessionKey;
	@JsonProperty("admin")
	private boolean admin;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
