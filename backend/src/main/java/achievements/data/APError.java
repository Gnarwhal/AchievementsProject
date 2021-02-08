package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class APError {

	@JsonProperty("code")
	private int code;
	@JsonProperty("message")
	private String message;

	public APError(int code) {
		this.code = code;
	}

	public APError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
