package achievements.apis;

import achievements.apis.steam.GetOwnedGameBody;
import achievements.apis.steam.GetPlayerAchievementsBody;
import achievements.apis.steam.GetSchemaForGameBody;
import achievements.data.APIResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

public class SteamAPI extends PlatformAPI {

	private String apiKey;

	public SteamAPI(int id, RestTemplate rest) {
		super(id, rest);
		try {
			var file = new FileInputStream("storage/apis/" + id + ".properties");
			var properties = new Properties();
			properties.load(file);

			apiKey = properties.getProperty("api-key");
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public APIResponse get(String userId) {
		var headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		var entity = new HttpEntity<String>(headers);
		var ownedGamesUrl    = UriComponentsBuilder.fromHttpUrl("http://api.steampowered.com/IPlayerService/GetOwnedGames/v1/")
			.queryParam("key", apiKey)
			.queryParam("steamid", userId)
			.queryParam("include_appinfo", true)
			.queryParam("include_played_free_games", true)
			.toUriString();

		var gameSchemaBaseUrl = UriComponentsBuilder.fromHttpUrl("https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/")
			.queryParam("key", apiKey);
		var playerAchievementsBaseUrl = UriComponentsBuilder.fromHttpUrl("https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v1/")
			.queryParam("key", apiKey)
			.queryParam("steamid", userId);

		var games = new ArrayList<APIResponse.Game>();
		try {
			var ownedResponse = rest.exchange(ownedGamesUrl, HttpMethod.GET, entity, GetOwnedGameBody.class).getBody();
			for (var game : ownedResponse.getResponse().getGames()) {
				var newGame = new APIResponse.Game();
				newGame.setPlatformGameId(Integer.toString(game.getAppid()));
				newGame.setName(game.getName());
				// Technically this is not the advertised logo url, but it's used be steamcommunity.com
				// and steamdb.info and it gives better aspect ratios and it means I don't need the random
				// logo_url field
				newGame.setThumbnail("https://cdn.cloudflare.steamstatic.com/steam/apps/" + game.getAppid() + "/header.jpg");
				newGame.setPlayed(game.getPlaytime_forever() > 0);

				var achievements = new HashMap<String, APIResponse.Game.Achievement>();

				var gameSchemaUrl = gameSchemaBaseUrl.cloneBuilder()
					.queryParam("appid", game.getAppid())
					.toUriString();
				var playerAchievementsUrl = playerAchievementsBaseUrl.cloneBuilder()
					.queryParam("appid", game.getAppid())
					.toUriString();

				try {
					var schemaResponse = rest.exchange(gameSchemaUrl, HttpMethod.GET, entity, GetSchemaForGameBody.class).getBody().getGame().getAvailableGameStats();
					if (schemaResponse != null && schemaResponse.getAchievements() != null) {
						for (var schema : schemaResponse.getAchievements()) {
							var achievement = new APIResponse.Game.Achievement();
							achievement.setName(schema.getDisplayName());
							achievement.setDescription(schema.getDescription());
							achievement.setStages(1);
							achievement.setThumbnail(schema.getIcon());
							achievements.put(schema.getName(), achievement);
						}

						var playerAchievementsResponse = rest.exchange(playerAchievementsUrl, HttpMethod.GET, entity, GetPlayerAchievementsBody.class).getBody().getPlayerstats().getAchievements();
						for (var achievement : playerAchievementsResponse) {
							achievements.get(achievement.getApiname()).setProgress(achievement.getAchieved());
						}

						newGame.setAchievements(new ArrayList<>(achievements.values()));
						if (newGame.getAchievements().size() > 0) {
							games.add(newGame);
						}
					}
				} catch (Exception e) {
					System.err.println("Forbidden APPID: " + game.getAppid());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		var response = new APIResponse();
		response.setGames(games);
		return response;
	}
}
