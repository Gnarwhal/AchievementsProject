package achievements.data;

import achievements.data.query.StringFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Profile {

	public static class Query {
		@JsonProperty("username")
		private StringFilter string;
	}

	@JsonProperty("id")
	private int id;
	@JsonProperty("username")
	private String username;
	@JsonProperty("plaforms")
	private List<String> platforms;
	@JsonProperty("games")
	private List<Game> games;
	@JsonProperty("achievements")
	private List<Achievement> achievements;

	public Profile(int id, String username, List<String> platforms, List<Game> games, List<Achievement> achievements) {
		this.id = id;
		this.username = username;
		this.platforms = platforms;
		this.games = games;
		this.achievements = achievements;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}
}
