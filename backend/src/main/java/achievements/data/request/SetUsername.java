package achievements.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetUsername {
	@JsonProperty("sessionKey")
	private String sessionKey;
	@JsonProperty("userId")
	private int userId;
	@JsonProperty("username")
	private String username;

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
