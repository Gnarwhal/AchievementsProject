package achievements.apis;

import achievements.data.APIResponse;
import org.springframework.web.client.RestTemplate;

public abstract class PlatformAPI {

	protected int id;
	protected RestTemplate rest;

	protected PlatformAPI(int id, RestTemplate rest) {
		this.id   = id;
		this.rest = rest;
	}

	public abstract APIResponse get(String userId);
}
