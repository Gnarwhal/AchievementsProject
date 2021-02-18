package achievements.services;

import achievements.data.request.SearchGames;
import achievements.data.request.SearchUsers;
import achievements.data.response.search.Achievement;
import achievements.data.request.SearchAchievements;
import achievements.data.response.search.Game;
import achievements.data.response.search.User;
import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Service
public class SearchService {

	@Autowired
	private DbConnection dbs;
	private Connection db;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	public List<Achievement> searchAchievements(SearchAchievements query) {
		try {
			var stmt = db.prepareCall("{call SearchAchievements(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			stmt.setString(1, query.getSearchTerm());
			stmt.setBoolean(3, query.isCompleted());
			if (query.getUserId()        != null) { stmt.setInt  (2, query.getUserId());        } else { stmt.setString(2, null); }
			if (query.getMinCompletion() != null) { stmt.setFloat(4, query.getMinCompletion()); } else { stmt.setString(4, null); }
			if (query.getMaxCompletion() != null) { stmt.setFloat(5, query.getMaxCompletion()); } else { stmt.setString(5, null); }
			if (query.getMinDifficulty() != null) { stmt.setFloat(6, query.getMinDifficulty()); } else { stmt.setString(6, null); }
			if (query.getMaxDifficulty() != null) { stmt.setFloat(7, query.getMaxDifficulty()); } else { stmt.setString(7, null); }
			if (query.getMinQuality()    != null) { stmt.setFloat(8, query.getMinQuality());    } else { stmt.setString(8, null); }
			if (query.getMaxQuality()    != null) { stmt.setFloat(9, query.getMaxQuality());    } else { stmt.setString(9, null); }
			stmt.setString(10, query.getOrdering());
			stmt.setString(11, query.getOrderDirection());
			var results = stmt.executeQuery();

			var achievements = new ArrayList<Achievement>();
			while (results.next()) {
				var achievement = new Achievement();
				achievement.setID        (results.getInt   ("ID"        ));
				achievement.setGame      (results.getString("Game"      ));
				achievement.setName      (results.getString("Name"      ));
				achievement.setCompletion(results.getInt   ("Completion")); if (results.wasNull()) { achievement.setCompletion(null); }
				achievement.setDifficulty(results.getFloat ("Difficulty")); if (results.wasNull()) { achievement.setDifficulty(null); }
				achievement.setQuality   (results.getFloat ("Quality"   )); if (results.wasNull()) { achievement.setQuality   (null); }
				achievements.add(achievement);
			}

			return achievements;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<User> searchUsers(SearchUsers query) {
		try {
			var stmt = db.prepareCall("{call SearchUsers(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			stmt.setString(1, query.getSearchTerm());
			if (query.getMinOwned()         != null) { stmt.setFloat(2, query.getMinOwned());         } else { stmt.setString(2, null); }
			if (query.getMaxOwned()         != null) { stmt.setFloat(3, query.getMaxOwned());         } else { stmt.setString(3, null); }
			if (query.getMinCompleted()     != null) { stmt.setFloat(4, query.getMinCompleted());     } else { stmt.setString(4, null); }
			if (query.getMaxCompleted()     != null) { stmt.setFloat(5, query.getMaxCompleted());     } else { stmt.setString(5, null); }
			if (query.getMinAvgCompletion() != null) { stmt.setFloat(6, query.getMinAvgCompletion()); } else { stmt.setString(6, null); }
			if (query.getMaxAvgCompletion() != null) { stmt.setFloat(7, query.getMaxAvgCompletion()); } else { stmt.setString(7, null); }
			stmt.setString(8, query.getOrdering());
			stmt.setString(9, query.getOrderDirection());
			var results = stmt.executeQuery();

			var users = new ArrayList<User>();
			while (results.next()) {
				var user = new User();
				user.setID               (results.getInt   ("ID"              ));
				user.setUsername         (results.getString("Username"        ));
				user.setGame_count       (results.getInt   ("GameCount"       ));
				user.setAchievement_count(results.getInt   ("AchievementCount"));
				user.setAvg_completion   (results.getInt   ("AvgCompletion"   )); if (results.wasNull()) { user.setAvg_completion(null); }
				user.setPerfect_games    (results.getInt   ("PerfectGames"    ));
				users.add(user);
			}

			return users;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Game> searchGames(SearchGames query) {
		try {
			var stmt = db.prepareCall("{call SearchGames(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			stmt.setString(1, query.getSearchTerm());
			stmt.setBoolean(3, query.isOwned());
			if (query.getUserId()           != null) { stmt.setInt  (2, query.getUserId());           } else { stmt.setString(2, null); }
			if (query.getMinAvgCompletion() != null) { stmt.setFloat(4, query.getMinAvgCompletion()); } else { stmt.setString(4, null); }
			if (query.getMaxAvgCompletion() != null) { stmt.setFloat(5, query.getMaxAvgCompletion()); } else { stmt.setString(5, null); }
			if (query.getMinNumOwners()     != null) { stmt.setFloat(6, query.getMinNumOwners());     } else { stmt.setString(6, null); }
			if (query.getMaxNumOwners()     != null) { stmt.setFloat(7, query.getMaxNumOwners());     } else { stmt.setString(7, null); }
			if (query.getMinNumPerfects()   != null) { stmt.setFloat(8, query.getMinNumPerfects());   } else { stmt.setString(8, null); }
			if (query.getMaxNumPerfects()   != null) { stmt.setFloat(9, query.getMaxNumPerfects());   } else { stmt.setString(9, null); }
			stmt.setString(10, query.getOrdering());
			stmt.setString(11, query.getOrderDirection());
			var results = stmt.executeQuery();

			var games = new ArrayList<Game>();
			while (results.next()) {
				var game = new Game();
				game.setID               (results.getInt   ("ID"              ));
				game.setName             (results.getString("Name"            ));
				game.setAchievement_count(results.getInt   ("AchievementCount"));
				game.setAvg_completion   (results.getInt   ("AvgCompletion"   )); if (results.wasNull()) { game.setAvg_completion(null); }
				game.setNum_owners       (results.getInt   ("NumOwners"       ));
				game.setNum_perfects     (results.getInt   ("NumPerfects"     ));
				games.add(game);
			}

			return games;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
