package achievements.misc;

import achievements.apis.PlatformAPI;
import achievements.apis.SteamAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class APIList {

	@Autowired
	private RestTemplate rest;

	public final Map<Integer, PlatformAPI> apis = new HashMap<>();

	@PostConstruct
	private void init() {
		/*db = dbs.getConnection();
		try {

			var stmt = db.prepareCall("{call GetPlatforms()}");
			var results = stmt.executeQuery();

			while (results.next()) {
				var id = results.getInt("ID");

				// Wanted to pull some skekery with dynamic class loading and external api jars, but...time is of the essence and I need to cut scope as much as possible
				apis.put(id, new ????(id, rest));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		apis.put(0, new SteamAPI(0, rest));
	}
}
