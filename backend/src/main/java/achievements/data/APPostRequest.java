package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class APPostRequest {

	@JsonProperty("key")
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
