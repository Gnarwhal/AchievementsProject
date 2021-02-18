package achievements.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchUsers {

	@JsonProperty("searchTerm")
	private String searchTerm;
	@JsonProperty("minOwned")
	private Float minOwned;
	@JsonProperty("maxOwned")
	private Float maxOwned;
	@JsonProperty("minCompleted")
	private Float minCompleted;
	@JsonProperty("maxCompleted")
	private Float maxCompleted;
	@JsonProperty("minAvgCompletion")
	private Float minAvgCompletion;
	@JsonProperty("maxAvgCompletion")
	private Float maxAvgCompletion;
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

	public Float getMinOwned() {
		return minOwned;
	}

	public void setMinOwned(Float minOwned) {
		this.minOwned = minOwned;
	}

	public Float getMaxOwned() {
		return maxOwned;
	}

	public void setMaxOwned(Float maxOwned) {
		this.maxOwned = maxOwned;
	}

	public Float getMinCompleted() {
		return minCompleted;
	}

	public void setMinCompleted(Float minCompleted) {
		this.minCompleted = minCompleted;
	}

	public Float getMaxCompleted() {
		return maxCompleted;
	}

	public void setMaxCompleted(Float maxCompleted) {
		this.maxCompleted = maxCompleted;
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
