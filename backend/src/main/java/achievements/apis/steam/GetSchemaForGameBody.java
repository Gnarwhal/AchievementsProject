package achievements.apis.steam;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetSchemaForGameBody {
	public static class Game {
		public static class GameStats {
			public static class Achievement {
				@JsonProperty("name")
				private String name;
				@JsonProperty("displayName")
				private String displayName;
				@JsonProperty("description")
				private String description;
				@JsonProperty("icon")
				private String icon;

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

				public String getDisplayName() {
					return displayName;
				}

				public void setDisplayName(String displayName) {
					this.displayName = displayName;
				}

				public String getDescription() {
					return description;
				}

				public void setDescription(String description) {
					this.description = description;
				}

				public String getIcon() {
					return icon;
				}

				public void setIcon(String icon) {
					this.icon = icon;
				}
			}

			@JsonProperty("achievements")
			private List<Achievement> achievements;

			public List<Achievement> getAchievements() {
				return achievements;
			}

			public void setAchievements(List<Achievement> achievements) {
				this.achievements = achievements;
			}
		}

		@JsonProperty("availableGameStats")
		private GameStats availableGameStats;

		public GameStats getAvailableGameStats() {
			return availableGameStats;
		}

		public void setAvailableGameStats(GameStats availableGameStats) {
			this.availableGameStats = availableGameStats;
		}
	}

	@JsonProperty("game")
	private Game game;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
