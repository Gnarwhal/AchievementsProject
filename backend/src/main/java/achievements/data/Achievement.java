package achievements.data;

import achievements.data.query.NumericFilter;
import achievements.data.query.StringFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Achievement {

	public static class Query {

		@JsonProperty("sessionKey")
		private String sessionKey;
		@JsonProperty("name")
		private StringFilter name;
		@JsonProperty("stages")
		private NumericFilter stages;
		@JsonProperty("completion")
		private NumericFilter completion;
		@JsonProperty("difficulty")
		private NumericFilter difficulty;
		@JsonProperty("quality")
		private NumericFilter quality;

		public Query(String sessionKey, StringFilter name, NumericFilter stages, NumericFilter completion, NumericFilter difficulty, NumericFilter quality) {
			this.sessionKey = sessionKey;
			this.name = name;
			this.stages = stages;
			this.completion = completion;
			this.difficulty = difficulty;
			this.quality = quality;
		}

		public String getSessionKey() {
			return sessionKey;
		}

		public void setSessionKey(String sessionKey) {
			this.sessionKey = sessionKey;
		}

		public StringFilter getName() {
			return name;
		}

		public void setName(StringFilter name) {
			this.name = name;
		}

		public NumericFilter getStages() {
			return stages;
		}

		public void setStages(NumericFilter stages) {
			this.stages = stages;
		}

		public NumericFilter getCompletion() {
			return completion;
		}

		public void setCompletion(NumericFilter completion) {
			this.completion = completion;
		}

		public NumericFilter getDifficulty() {
			return difficulty;
		}

		public void setDifficulty(NumericFilter difficulty) {
			this.difficulty = difficulty;
		}

		public NumericFilter getQuality() {
			return quality;
		}

		public void setQuality(NumericFilter quality) {
			this.quality = quality;
		}
	}

	@JsonProperty("ID")
	private int id;
	@JsonProperty("game")
	private int gameId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("stages")
	private int stages;
	@JsonProperty("completion")
	private float completion;
	@JsonProperty("difficulty")
	private float difficulty;
	@JsonProperty("quality")
	private float quality;

	public Achievement(int id, int gameId, String name, String description, int stages, float completion, float difficulty, float quality) {
		this.id = id;
		this.gameId = gameId;
		this.name = name;
		this.description = description;
		this.stages = stages;
		this.completion = completion;
		this.difficulty = difficulty;
		this.quality = quality;
	}

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public int getStages() { return stages; }

	public void setStages(int stages) { this.stages = stages; }

	public float getCompletion() {
		return completion;
	}

	public void setCompletion(float completion) {
		this.completion = completion;
	}

	public float getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(float difficulty) {
		this.difficulty = difficulty;
	}

	public float getQuality() {
		return quality;
	}

	public void setQuality(float quality) {
		this.quality = quality;
	}
}
