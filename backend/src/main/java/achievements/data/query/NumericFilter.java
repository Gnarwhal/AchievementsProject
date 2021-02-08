package achievements.data.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NumericFilter {

	@JsonProperty("min")
	private Float min;
	@JsonProperty("max")
	private Float max;

	public NumericFilter(Float min, Float max) {
		this.min = min;
		this.max = max;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}
}
