package achievements.services;

import achievements.data.Achievements;
import achievements.data.Games;
import achievements.data.User;
import achievements.misc.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.*;

@Service
public class DbService {

	@Autowired
	private DbConnectionService dbs;
	private Connection db;

	@PostConstruct
	private void init() { db = dbs.getConnection(); }

	public Achievements getAchievements(String gameName) {
		try {
			// Create Query
			CallableStatement stmt = db.prepareCall("{? = call GetAchievements(?)}");
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, gameName);

			// Read Result(s)
			ResultSet results = stmt.executeQuery();
			var achievements = new Achievements();
			while (results.next()) {
				// Add Result(s) to data class
				int    achievementGameID      = results.getInt("GameID");
				String achievementGameName    = results.getString("GameName");
				String achievementName        = results.getString("Name");
				String achievementDescription = results.getString("Description");
				int    achievementStages      = results.getInt("Stages");
				// Checks if getting from specific game or all achievements
				if (!gameName.equals("%")) {
					achievements.setGameID(achievementGameID);
					achievements.setGameName(achievementGameName);
				}
				achievements.addAchievement(new Achievements.Achievement(achievementName, achievementDescription, achievementStages));
			}
			stmt.close();
			return achievements;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Games getGames(String name) {
		try {
			// Create Query
			CallableStatement stmt = db.prepareCall("{? = call GetGame(?)}");
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, name);

			// Read Result(s)
			ResultSet results = stmt.executeQuery();
			var games = new Games();
			while (results.next()) {
				// Add Result(s) to data class
				int    gameID       = results.getInt("ID");
				String gameName     = results.getString("Name");
				String gamePlatform = results.getString("PlatformName");
				if (!games.getGames().isEmpty()) {
					var lastGame = games.getGames().get(games.getGames().size()-1);
					if (lastGame.getId() == gameID) {
						lastGame.addToPlatforms(gamePlatform);
					} else {
						games.addGame(new Games.Game(gameID,gameName,gamePlatform));
					}
				} else {
					games.addGame(new Games.Game(gameID,gameName,gamePlatform));
				}

			}
			stmt.close();
			return games;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int createUser(User user) {
		try {
			var statement = db.prepareCall("{? = call CreateUser(?, ?, ?, ?)}");
			statement.registerOutParameter(1, Types.INTEGER);
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getUsername());

			var password = Password.generate(user.getPassword());
			statement.setString(4, password.salt);
			statement.setString(5, password.hash);

			statement.execute();
			var code = statement.getInt(1);
			statement.close();

			return code;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int login(User user) {
		try {
			var statement = db.prepareStatement("SELECT Salt, Password FROM [dbo].[User] WHERE Email = ?");
			statement.setString(1, user.email);

			var result = statement.executeQuery();
			if (result.next()) {
				var salt = result.getString("Salt");
				var hash = result.getString("Password");
				if (Password.validate(salt, user.getPassword(), hash)) {
					return 0;
				} else {
					return 2;
				}
			} else {
				return 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
