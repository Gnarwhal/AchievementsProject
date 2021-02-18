package achievements.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchAchievements {

	@JsonProperty("searchTerm")
	private String searchTerm;
	@JsonProperty("userId")
	private Integer userId;
	@JsonProperty("completed")
	private boolean completed;
	@JsonProperty("minCompletion")
	private Float minCompletion;
	@JsonProperty("maxCompletion")
	private Float maxCompletion;
	@JsonProperty("minDifficulty")
	private Float minDifficulty;
	@JsonProperty("maxDifficulty")
	private Float maxDifficulty;
	@JsonProperty("minQuality")
	private Float minQuality;
	@JsonProperty("maxQuality")
	private Float maxQuality;
	@JsonProperty("ordering")
	private String ordering;
	@JsonProperty("orderDirection")
	private String orderDirection;

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Float getMinCompletion() {
		return minCompletion;
	}

	public void setMinCompletion(Float minCompletion) {
		this.minCompletion = minCompletion;
	}

	public Float getMaxCompletion() {
		return maxCompletion;
	}

	public void setMaxCompletion(Float maxCompletion) {
		this.maxCompletion = maxCompletion;
	}

	public Float getMinDifficulty() {
		return minDifficulty;
	}

	public void setMinDifficulty(Float minDifficulty) {
		this.minDifficulty = minDifficulty;
	}

	public Float getMaxDifficulty() {
		return maxDifficulty;
	}

	public void setMaxDifficulty(Float maxDifficulty) {
		this.maxDifficulty = maxDifficulty;
	}

	public Float getMinQuality() {
		return minQuality;
	}

	public void setMinQuality(Float minQuality) {
		this.minQuality = minQuality;
	}

	public Float getMaxQuality() {
		return maxQuality;
	}

	public void setMaxQuality(Float maxQuality) {
		this.maxQuality = maxQuality;
	}

	public String getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
}
