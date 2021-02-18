package achievements.data.response.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

	@JsonProperty("ID")
	private int ID;
	@JsonProperty("username")
	private String username;
	@JsonProperty("game_count")
	private int game_count;
	@JsonProperty("achievement_count")
	private int achievement_count;
	@JsonProperty("avg_completion")
	private Integer avg_completion;
	@JsonProperty("perfect_games")
	private int perfect_games;

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getGame_count() {
		return game_count;
	}

	public void setGame_count(int game_count) {
		this.game_count = game_count;
	}

	public int getAchievement_count() {
		return achievement_count;
	}

	public void setAchievement_count(int achievement_count) {
		this.achievement_count = achievement_count;
	}

	public Integer getAvg_completion() {
		return avg_completion;
	}

	public void setAvg_completion(Integer avg_completion) {
		this.avg_completion = avg_completion;
	}

	public int getPerfect_games() {
		return perfect_games;
	}

	public void setPerfect_games(int perfect_games) {
		this.perfect_games = perfect_games;
	}
}
