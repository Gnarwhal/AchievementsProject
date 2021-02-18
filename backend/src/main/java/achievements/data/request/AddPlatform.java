package achievements.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddPlatform {
	@JsonProperty("sessionKey")
	private String sessionKey;
	@JsonProperty("platformId")
	private int platformId;
	@JsonProperty("platformUserId")
	private String platformUserId;

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

	public String getPlatformUserId() {
		return platformUserId;
	}

	public void setPlatformUserId(String platformUserId) {
		this.platformUserId = platformUserId;
	}
}
