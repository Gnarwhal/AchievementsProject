package achievements.data.importing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportPlatform {

	@JsonProperty("userId")
	private int userId;
	@JsonProperty("sessionKey")
	private String sessionKey;
	@JsonProperty("name")
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
