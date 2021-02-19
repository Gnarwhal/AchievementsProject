package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

	@JsonProperty("email")
	public String email;
	@JsonProperty("username")
	public String username;
	@JsonProperty("password")
	public String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
