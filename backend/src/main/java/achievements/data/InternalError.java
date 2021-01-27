package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalError {

	@JsonProperty
	private String message;

	public InternalError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
