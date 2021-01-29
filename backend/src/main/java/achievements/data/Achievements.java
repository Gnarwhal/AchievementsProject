package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Achievements {

	public static class Achievement {

		@JsonProperty("Name")
		private String name;
		@JsonProperty("Description")
		private String description;
		@JsonProperty("Stages")
		private int stages;

		public Achievement(String name, String description, int stages) {
			this.name        = name;
			this.description = description;
			this.stages      = stages;
		}

		// Start Getters/Setters
		public String getName() { return name; }

		public void setName(String name) { this.name = name; }

		public String getDescription() { return description; }

		public void setDescription(String description) { this.description = description; }

		public int getStages() { return stages; }

		public void setStages(int stages) { this.stages = stages; }
		// End Getters/Setters
	}

	@JsonProperty("GameID")
	private int gameID;
	@JsonProperty("GameName")
	private String gameName;
	@JsonProperty("Achievements")
	private List<Achievement> achievements;

	public Achievements() { achievements = new ArrayList<Achievement>(); }

	// Start Getters/Setters
	public int getGameID() { return gameID; }

	public void setGameID(int gameID) { this.gameID = gameID; }

	public String getGameName() { return gameName; }

	public void setGameName(String gameName) { this.gameName = gameName; }

	public List<Achievement> getAchievements() { return achievements; }

	public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }
	// End Getters/Setters

	public void addAchievement(Achievement achievement) { this.achievements.add(achievement); };
}
