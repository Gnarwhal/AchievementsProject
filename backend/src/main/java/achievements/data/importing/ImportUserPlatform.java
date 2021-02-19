package achievements.data.importing;

import achievements.data.request.AddPlatform;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportUserPlatform extends AddPlatform {

	@JsonProperty("userId")
	private int userId;
	@JsonProperty("userEmail")
	private String userEmail;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
