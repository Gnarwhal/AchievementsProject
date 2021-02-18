package achievements.data.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemovePlatform {
	@JsonProperty("sessionKey")
	private String sessionKey;
	@JsonProperty("platformId")
	private int platformId;

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
}
