package achievements.services;

import achievements.misc.APIList;
import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Types;

@Service
public class APIService {

	@Autowired
	private RestTemplate rest;
	@Autowired
	private APIList apis;
	@Autowired
	private DbConnection dbs;
	private Connection db;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	private String getFileType(String imagePath) {
		var path = imagePath.split("\\.");
		return path[path.length - 1];
	}

	public int importUserPlatform(int userId, int platformId, String platformUserId) {
		try {
			var response = apis.apis.get(platformId).get(platformUserId);

			var addIfNotGame = db.prepareCall("{call AddIfNotGame(?, ?, ?)}");
			var addGameToPlatform = db.prepareCall("{call AddGameToPlatform(?, ?, ?)}");
			var addGameToUser = db.prepareCall("{call AddGameToPlatform(?, ?, ?)}");
			var addIfNotAchievement = db.prepareCall("{call AddIfNotAchievement(?, ?, ?, ?, ?, ?)}");
			var setAchievementProgressForUser = db.prepareCall("{call SetAchievementProgressForUser(?, ?, ?, ?)}");

			addIfNotGame.registerOutParameter(3, Types.INTEGER);
			addIfNotAchievement.registerOutParameter(6, Types.INTEGER);

			for (var game : response.getGames()) {
				addIfNotGame.setString(1, game.getName());
				addIfNotGame.setString(2, getFileType(game.getThumbnail()));
				addIfNotGame.execute();
				var gameId = addIfNotGame.getInt(3);

				addGameToPlatform.setInt(1, gameId);
				addGameToPlatform.setInt(2, platformId);
				addGameToPlatform.setString(3, platformUserId);
				addGameToPlatform.execute();

				var gameThumbnail = new File("storage/images/game/" + gameId + "." + getFileType(game.getThumbnail()));
				if (!gameThumbnail.exists()) {
					var bytes = rest.getForObject(game.getThumbnail(), byte[].class);
					var stream = new FileOutputStream(gameThumbnail);
					stream.write(bytes);
					stream.close();
				}

				addGameToUser.setInt(1, gameId);
				addGameToUser.setInt(2, userId);
				addGameToUser.setInt(3, platformId);
				addGameToUser.execute();

				for (var achievement : game.getAchievements()) {
					addIfNotAchievement.setInt(1, gameId);
					addIfNotAchievement.setString(2, achievement.getName());
					addIfNotAchievement.setString(3, achievement.getDescription());
					addIfNotAchievement.setInt(4, achievement.getStages());
					addIfNotAchievement.setString(5, getFileType(achievement.getThumbnail()));
					addIfNotAchievement.execute();
					var achievementId = addIfNotAchievement.getInt(6);

					var achievementIcon = new File("storage/images/achievement/" + achievementId + "." + getFileType(achievement.getThumbnail()));
					if (!achievementIcon.exists()) {
						var bytes = rest.getForObject(achievement.getThumbnail(), byte[].class);
						var stream = new FileOutputStream(achievementIcon);
						stream.write(bytes);
						stream.close();
					}

					if (game.isPlayed()) {
						setAchievementProgressForUser.setInt(1, userId);
						setAchievementProgressForUser.setInt(2, platformId);
						setAchievementProgressForUser.setInt(3, achievementId);
						setAchievementProgressForUser.setInt(4, achievement.getProgress());
						setAchievementProgressForUser.execute();
					}
				}
			}

			addIfNotGame.close();
			addGameToPlatform.close();
			addIfNotAchievement.close();
			setAchievementProgressForUser.close();

			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
