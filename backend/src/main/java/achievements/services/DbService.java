package achievements.services;

import achievements.data.Achievements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DbService {

	@Autowired
	private DbConnectionService dbs;
	private Connection db;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	public Achievements getAchievements() {
		final String QUERY = "SELECT * FROM [dbo].[Achievement]";

		try {
			var statement    = db.createStatement();
			var achievements = new Achievements();
			var queryResults = statement.executeQuery(QUERY);

			while (queryResults.next()) {
				achievements.getAchievements().add(new Achievements.Achievement(
					queryResults.getInt("GameID"),
					queryResults.getString("Name"),
					queryResults.getString("Description"),
					queryResults.getInt("Stages")
				));
			}

			statement.close();
			return achievements;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
