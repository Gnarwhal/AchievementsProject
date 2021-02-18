package achievements.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Achievement {

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
}
