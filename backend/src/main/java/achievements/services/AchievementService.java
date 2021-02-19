package achievements.services;

import achievements.data.request.RateAchievement;
import achievements.data.response.search.Achievement;
import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;

@Service
public class AchievementService {

	@Autowired
	private DbConnection dbs;
	private Connection   db;

	@Autowired
	private ImageService imageService;

	@Autowired
	private AuthenticationService authService;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	public String[] getIcon(int achievementId) {
		try {
			var stmt = db.prepareCall("{call GetAchievementIcon(?)}");
			return imageService.getImageType(stmt, achievementId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Achievement getAchievement(int achievementId) {
		try {
			var stmt = db.prepareCall("{call GetAchievement(?)}");
			stmt.setInt(1, achievementId);

			var result = stmt.executeQuery();
			if (result.next()) {
				var achievement = new Achievement();
				achievement.setID(result.getInt("ID"));
				achievement.setName(result.getString("Name"));
				achievement.setCompletion(result.getInt("Completion")); if (result.wasNull()) { achievement.setCompletion(null); }
				achievement.setDescription(result.getString("Description"));
				achievement.setDifficulty(result.getFloat("Difficulty")); if (result.wasNull()) { achievement.setDifficulty(null); }
				achievement.setQuality(result.getFloat("Quality")); if (result.wasNull()) { achievement.setQuality(null); }

				stmt = db.prepareCall("{call GetRatingsForAchievement(?)}");
				stmt.setInt(1, achievementId);

				var ratings = new ArrayList<Achievement.Rating>();
				var results = stmt.executeQuery();
				while (results.next()) {
					var rating = new Achievement.Rating();
					rating.setUserId(results.getInt("UserID"));
					rating.setUsername(results.getString("Username"));
					rating.setDifficulty(results.getFloat("Difficulty")); if (results.wasNull()) { rating.setDifficulty(null); }
					rating.setQuality(results.getFloat("Quality")); if (results.wasNull()) { rating.setQuality(null); }
					rating.setReview(results.getString("Description"));
					ratings.add(rating);
				}
				achievement.setRatings(ratings);
				return achievement;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public RateAchievement getRating(int achievement, int user) {
		try {
			var stmt = db.prepareCall("{call HasProgress(?, ?, ?)}");
			stmt.setInt(1, user);
			stmt.setInt(2, achievement);
			stmt.registerOutParameter(3, Types.BOOLEAN);

			stmt.execute();
			if (stmt.getBoolean(3)) {
				stmt = db.prepareCall("{call GetRating(?, ?)}");
				stmt.setInt(1, user);
				stmt.setInt(2, achievement);

				var result = stmt.executeQuery();
				if (result.next()) {
					var rating = new RateAchievement();
					rating.setDifficulty(result.getFloat("Difficulty")); if (result.wasNull()) { rating.setDifficulty(null); }
					rating.setQuality(result.getFloat("Quality")); if (result.wasNull()) { rating.setQuality(null); }
					rating.setReview(result.getString("Description"));
					return rating;
				} else {
					return new RateAchievement();
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public RateAchievement setRating(int achievementId, int userId, RateAchievement rateAchievement) {
		if (authService.session().validate(userId, rateAchievement.getSessionKey())) {
			try {
				var stmt = db.prepareCall("{call SetRating(?, ?, ?, ?, ?)}");
				stmt.setInt(1, userId);
				stmt.setInt(2, achievementId);
				stmt.setFloat(3, rateAchievement.getDifficulty());
				stmt.setFloat(4, rateAchievement.getQuality());
				stmt.setString(5, rateAchievement.getReview());

				stmt.execute();
				return rateAchievement;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			var stmt = db.prepareCall("{call GetRating(?, ?)}");
			stmt.setInt(1, userId);
			stmt.setInt(2, achievementId);

			var result = stmt.executeQuery();
			if (result.next()) {
				var rating = new RateAchievement();
				rating.setDifficulty(result.getFloat("Difficulty"));
				rating.setQuality(result.getFloat("Quality"));
				rating.setReview(result.getString("Review"));
				return rating;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
