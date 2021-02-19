package achievements.data;

import achievements.data.response.search.Achievement;
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

	public static class Rating {
		@JsonProperty("achievementId")
		private int achievementId;
		@JsonProperty("name")
		private String name;
		@JsonProperty("difficulty")
		private Float difficulty;
		@JsonProperty("quality")
		private Float quality;
		@JsonProperty("review")
		private String review;

		public int getAchievementId() {
			return achievementId;
		}

		public void setAchievementId(int achievementId) {
			this.achievementId = achievementId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Float getDifficulty() {
			return difficulty;
		}

		public void setDifficulty(Float difficulty) {
			this.difficulty = difficulty;
		}

		public Float getQuality() {
			return quality;
		}

		public void setQuality(Float quality) {
			this.quality = quality;
		}

		public String getReview() {
			return review;
		}

		public void setReview(String review) {
			this.review = review;
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
	@JsonProperty("ratings")
	private List<Rating> ratings;

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

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}
}
