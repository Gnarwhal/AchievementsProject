package achievements.data;

import achievements.data.query.StringFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Profile {

	public static class Platform {
		@JsonProperty
		private int id;
		@JsonProperty("name")
		private String name;
		@JsonProperty("connected")
		private boolean connected;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean getConnected() {
			return connected;
		}

		public void setConnected(boolean connected) {
			this.connected = connected;
		}
	}

	@JsonProperty("username")
	private String username;
	@JsonProperty("completed")
	private int completed;
	@JsonProperty("average")
	private Integer average;
	@JsonProperty("perfect")
	private int perfect;
	@JsonProperty("noteworthy")
	private List<Achievement> noteworthy;
	@JsonProperty("platforms")
	private List<Platform> platforms;
	/*@JsonProperty("ratings")
	private List<Rating> ratings;*/

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public Integer getAverage() {
		return average;
	}

	public void setAverage(Integer average) {
		this.average = average;
	}

	public int getPerfect() {
		return perfect;
	}

	public void setPerfect(int perfect) {
		this.perfect = perfect;
	}

	public List<Achievement> getNoteworthy() {
		return noteworthy;
	}

	public void setNoteworthy(List<Achievement> noteworthy) {
		this.noteworthy = noteworthy;
	}

	public List<Platform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<Platform> platforms) {
		this.platforms = platforms;
	}
}
