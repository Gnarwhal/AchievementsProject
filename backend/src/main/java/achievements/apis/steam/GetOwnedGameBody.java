package achievements.apis.steam;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetOwnedGameBody {

	public static class Response {

		public static class Game {
			@JsonProperty("appid")
			private int appid;
			@JsonProperty("name")
			private String name;
			@JsonProperty("playtime_forever")
			private int playtime_forever;
			@JsonProperty("img_icon_url")
			private String img_icon_url;
			@JsonProperty("img_logo_url")
			private String img_logo_url;

			public int getAppid() {
				return appid;
			}

			public void setAppid(int appid) {
				this.appid = appid;
			}

			public int getPlaytime_forever() {
				return playtime_forever;
			}

			public void setPlaytime_forever(int playtime_forever) {
				this.playtime_forever = playtime_forever;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getImg_icon_url() {
				return img_icon_url;
			}

			public void setImg_icon_url(String img_icon_url) {
				this.img_icon_url = img_icon_url;
			}

			public String getImg_logo_url() {
				return img_logo_url;
			}

			public void setImg_logo_url(String img_logo_url) {
				this.img_logo_url = img_logo_url;
			}
		}

		@JsonProperty("game_count")
		private int game_count;
		@JsonProperty("games")
		private List<Game> games;

		public int getGame_count() {
			return game_count;
		}

		public void setGame_count(int game_count) {
			this.game_count = game_count;
		}

		public List<Game> getGames() {
			return games;
		}

		public void setGames(List<Game> games) {
			this.games = games;
		}
	}

	@JsonProperty("response")
	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
}
