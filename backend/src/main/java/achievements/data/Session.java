package achievements.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Session {

	@JsonProperty("key")
	private String key;
	@JsonProperty("id")
	private int id;
	@JsonProperty("hue")
	private int hue;
	@JsonIgnore
	private boolean used;

	public Session(String key, int id, int hue) {
		this.key  = key;
		this.id   = id;
		this.hue  = hue;
		this.used = false;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHue() {
		return hue;
	}

	public void setHue(int hue) {
		this.hue = hue;
	}

	public boolean getUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
}
