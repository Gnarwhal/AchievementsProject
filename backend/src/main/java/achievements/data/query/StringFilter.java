package achievements.data.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StringFilter {

	@JsonProperty("query")
	private String query;

	public StringFilter(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
