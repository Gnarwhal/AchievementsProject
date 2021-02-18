package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class APIResponse {

	public static class Game {

		public static class Achievement {
			@JsonProperty("name")
			private String name;
			@JsonProperty("description")
			private String description;
			@JsonProperty("stages")
			private int stages;
			@JsonProperty("progress")
			private int progress;
			@JsonProperty("thumbnail")
			private String thumbnail;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getDescription() {
				return description;
			}

			public void setDescription(String description) {
				this.description = description;
			}

			public int getStages() {
				return stages;
			}

			public void setStages(int stages) {
				this.stages = stages;
			}

			public int getProgress() {
				return progress;
			}

			public void setProgress(int progress) {
				this.progress = progress;
			}

			public String getThumbnail() {
				return thumbnail;
			}

			public void setThumbnail(String thumbnail) {
				this.thumbnail = thumbnail;
			}
		}

		@JsonProperty("platformGameId")
		private String platformGameId;
		@JsonProperty("name")
		private String name;
		@JsonProperty("played")
		private boolean played;
		@JsonProperty("thumbnail")
		private String thumbnail;
		@JsonProperty("achievements")
		private List<Achievement> achievements;

		public String getPlatformGameId() {
			return platformGameId;
		}

		public void setPlatformGameId(String platformGameId) {
			this.platformGameId = platformGameId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isPlayed() {
			return played;
		}

		public void setPlayed(boolean played) {
			this.played = played;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public List<Achievement> getAchievements() {
			return achievements;
		}

		public void setAchievements(List<Achievement> achievements) {
			this.achievements = achievements;
		}
	}

	@JsonProperty("games")
	private List<Game> games;

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}
}
