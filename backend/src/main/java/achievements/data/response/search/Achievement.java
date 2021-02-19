package achievements.data.response.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Achievement {

	public static class Rating {
		@JsonProperty("userId")
		private int userId;
		@JsonProperty("username")
		private String username;
		@JsonProperty("difficulty")
		private Float difficulty;
		@JsonProperty("quality")
		private Float quality;
		@JsonProperty("review")
		private String review;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
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

	@JsonProperty("ID")
	private int ID;
	@JsonProperty("game")
	private String game;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("completion")
	private Integer completion;
	@JsonProperty("difficulty")
	private Float difficulty;
	@JsonProperty("quality")
	private Float quality;
	@JsonProperty("ratings")
	private List<Rating> ratings;

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public Integer getCompletion() {
		return completion;
	}

	public void setCompletion(Integer completion) {
		this.completion = completion;
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

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}
}
