package achievements.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchGames {

	@JsonProperty("searchTerm")
	private String searchTerm;
	@JsonProperty("userId")
	private Integer userId;
	@JsonProperty("owned")
	private boolean owned;
	@JsonProperty("minAvgCompletion")
	private Float minAvgCompletion;
	@JsonProperty("maxAvgCompletion")
	private Float maxAvgCompletion;
	@JsonProperty("minNumOwners")
	private Float minNumOwners;
	@JsonProperty("maxNumOwners")
	private Float maxNumOwners;
	@JsonProperty("minNumPerfects")
	private Float minNumPerfects;
	@JsonProperty("maxNumPerfects")
	private Float maxNumPerfects;
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

	public boolean isOwned() {
		return owned;
	}

	public void setOwned(boolean owned) {
		this.owned = owned;
	}

	public Float getMinAvgCompletion() {
		return minAvgCompletion;
	}

	public void setMinAvgCompletion(Float minAvgCompletion) {
		this.minAvgCompletion = minAvgCompletion;
	}

	public Float getMaxAvgCompletion() {
		return maxAvgCompletion;
	}

	public void setMaxAvgCompletion(Float maxAvgCompletion) {
		this.maxAvgCompletion = maxAvgCompletion;
	}

	public Float getMinNumOwners() {
		return minNumOwners;
	}

	public void setMinNumOwners(Float minNumOwners) {
		this.minNumOwners = minNumOwners;
	}

	public Float getMaxNumOwners() {
		return maxNumOwners;
	}

	public void setMaxNumOwners(Float maxNumOwners) {
		this.maxNumOwners = maxNumOwners;
	}

	public Float getMinNumPerfects() {
		return minNumPerfects;
	}

	public void setMinNumPerfects(Float minNumPerfects) {
		this.minNumPerfects = minNumPerfects;
	}

	public Float getMaxNumPerfects() {
		return maxNumPerfects;
	}

	public void setMaxNumPerfects(Float maxNumPerfects) {
		this.maxNumPerfects = maxNumPerfects;
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
