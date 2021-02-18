package achievements.services;

import achievements.data.Achievement;
import achievements.data.query.SearchAchievements;
import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
			var stmt = db.prepareCall("{call SearchAchievements(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			stmt.setString(1, query.getSearchTerm());
			stmt.setBoolean(3, query.isCompleted());
			if (query.getUserId()        != null) { stmt.setInt(  2, query.getUserId()       ); } else { stmt.setString(2, null); }
			if (query.getMinCompletion() != null) { stmt.setFloat(4, query.getMinCompletion()); } else { stmt.setString(4, null); }
			if (query.getMaxCompletion() != null) { stmt.setFloat(5, query.getMaxCompletion()); } else { stmt.setString(5, null); }
			if (query.getMinDifficulty() != null) { stmt.setFloat(6, query.getMinDifficulty()); } else { stmt.setString(6, null); }
			if (query.getMaxDifficulty() != null) { stmt.setFloat(7, query.getMaxDifficulty()); } else { stmt.setString(7, null); }
			if (query.getMinQuality()    != null) { stmt.setFloat(8, query.getMinQuality()   ); } else { stmt.setString(8, null); }
			if (query.getMaxQuality()    != null) { stmt.setFloat(9, query.getMaxQuality()   ); } else { stmt.setString(9, null); }
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
}
