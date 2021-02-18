package achievements.apis.steam;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetPlayerAchievementsBody {
	public static class PlayerStats {
		public static class Achievement {
			@JsonProperty("apiname")
			private String apiname;
			@JsonProperty("achieved")
			private int achieved;

			public String getApiname() {
				return apiname;
			}

			public void setApiname(String apiname) {
				this.apiname = apiname;
			}

			public int getAchieved() {
				return achieved;
			}

			public void setAchieved(int achieved) {
				this.achieved = achieved;
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

	@JsonProperty("playerstats")
	private PlayerStats playerstats;

	public PlayerStats getPlayerstats() {
		return playerstats;
	}

	public void setPlayerstats(PlayerStats playerstats) {
		this.playerstats = playerstats;
	}
}
