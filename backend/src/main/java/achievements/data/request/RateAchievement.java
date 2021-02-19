package achievements.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RateAchievement {

	@JsonProperty("sessionKey")
	private String sessionKey;
	@JsonProperty("difficulty")
	private Float difficulty;
	@JsonProperty("quality")
	private Float quality;
	@JsonProperty("review")
	private String review;

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
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
