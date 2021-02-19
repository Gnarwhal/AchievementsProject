package achievements.services;

import achievements.data.Profile;
import achievements.data.request.AddPlatform;
import achievements.data.request.RemovePlatform;
import achievements.data.request.SetUsername;
import achievements.data.response.search.Achievement;
import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static achievements.services.ImageService.MIME_TO_EXT;

@Service
public class UserService {

	@Autowired
	private DbConnection dbs;
	private Connection db;

	@Autowired
	private AuthenticationService auth;

	@Autowired
	private APIService apiService;

	@Autowired
	private ImageService imageService;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	public Profile getProfile(int userId) {
		try {
			var profile = (Profile) null;
			{
				var stmt = db.prepareCall("{? = call GetUserNameAndStats(?, ?, ?, ?, ?)}");
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, userId);
				stmt.registerOutParameter(3, Types.VARCHAR);
				stmt.registerOutParameter(4, Types.INTEGER);
				stmt.registerOutParameter(5, Types.INTEGER);
				stmt.registerOutParameter(6, Types.INTEGER);

				stmt.execute();
				if (stmt.getInt(1) == 0) {
					profile = new Profile();
					profile.setUsername(stmt.getString(3));
					profile.setCompleted(stmt.getInt(4));
					var average = stmt.getString(5);
					profile.setPerfect(stmt.getInt(6));

					if (average != null) {
						profile.setAverage(Integer.parseInt(average));
					}
				} else {
					return null;
				}
			}

			{
				var stmt = db.prepareCall("{call GetUserPlatforms(?)}");
				stmt.setInt(1, userId);

				var results = stmt.executeQuery();
				var platforms = new ArrayList<Profile.Platform>();
				while (results.next()) {
					var platform = new Profile.Platform();
					platform.setId       (results.getInt    ("ID"          ));
					platform.setName     (results.getString ("PlatformName"));
					platform.setConnected(results.getBoolean("Connected"   ));
					platforms.add(platform);
				}
				profile.setPlatforms(platforms);
			}

			{
				var stmt = db.prepareCall("{call GetRatingsByUser(?)}");
				stmt.setInt(1, userId);

				var results = stmt.executeQuery();
				var ratings = new ArrayList<Profile.Rating>();
				while (results.next()) {
					var rating = new Profile.Rating();
					rating.setAchievementId(results.getInt("AchievementID"));
					rating.setName(results.getString("Name"));
					rating.setDifficulty(results.getFloat("Difficulty")); if (results.wasNull()) { rating.setDifficulty(null); }
					rating.setQuality(results.getFloat("Quality")); if (results.wasNull()) { rating.setQuality(null); }
					rating.setReview(results.getString("Description"));
					ratings.add(rating);
				}
				profile.setRatings(ratings);
			}

			return profile;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int setUsername(int userId, SetUsername username) {
		try {
			if (auth.session().validate(userId, username.getSessionKey()) && username.getUsername().length() > 0 && username.getUsername().length() <= 32) {
				var stmt = db.prepareCall("{call SetUsername(?, ?)}");
				stmt.setInt(1, userId);
				stmt.setString(2, username.getUsername());

				stmt.execute();
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public String[] getProfileImage(int userId) {
		try {
			var stmt = db.prepareCall("{call GetUserImage(?)}");
			return imageService.getImageType(stmt, userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String setProfileImage(int userId, String sessionKey, MultipartFile file) {
		try {
			var type = file.getContentType();
			if (type.matches("image/.*")) {
				type = type.substring(6);
				type = MIME_TO_EXT.get(type);
				if (!auth.session().validate(userId, sessionKey)) {
					return "forbidden";
				} else if (type == null) {
					return "unsupported_type";
				} else {
					var stmt = db.prepareCall("{call SetUserImage(?, ?, ?)}");
					stmt.setInt(1, userId);
					stmt.setString(2, type);
					stmt.registerOutParameter(3, Types.VARCHAR);

					stmt.execute();
					var oldType = stmt.getString(3);

					// Delete old file
					if (oldType != null && type != oldType) {
						var oldFile = new File("storage/images/user/" + userId + "." + oldType);
						if (oldFile.exists()) {
							oldFile.delete();
						}
					}

					// Save new file (will overwrite old if file type didn't change)
					{
						var image = new FileOutputStream("storage/images/user/" + userId + "." + type);
						FileCopyUtils.copy(file.getInputStream(), image);
						image.close();
					}

					return "success";
				}
			} else {
				return "not_an_image";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "unknown";
	}

	public int addPlatform(int userId, AddPlatform request, boolean validate) {
		if (!validate || auth.session().validate(userId, request.getSessionKey())) {
			try {
				db.setAutoCommit(false);
				try {
					var stmt = db.prepareCall("{call AddUserToPlatform(?, ?, ?)}");
					stmt.setInt(1, userId);
					stmt.setInt(2, request.getPlatformId());
					stmt.setString(3, request.getPlatformUserId());

					stmt.execute();

					int successful = apiService.importUserPlatform(userId, request.getPlatformId(), request.getPlatformUserId());

					if (successful == 0) {
						db.commit();
						db.setAutoCommit(true);
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				db.rollback();
				db.setAutoCommit(true);
			} catch(SQLException e){
				e.printStackTrace();
			}
		}
		return -1;
	}

	public int removePlatform(int userId, RemovePlatform request) {
		try {
			if (auth.session().validate(userId, request.getSessionKey())) {
				var stmt = db.prepareCall("{call RemoveUserFromPlatform(?, ?)}");
				stmt.setInt(1, userId);
				stmt.setInt(2, request.getPlatformId());

				stmt.execute();

				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<Achievement> getNoteworthy(int userId) {
		try {
			var stmt = db.prepareCall("{call GetNoteworthyAchievementsForUser(?)}");
			stmt.setInt(1, userId);

			var results = stmt.executeQuery();
			var achievements = new ArrayList<Achievement>();
			while (results.next()) {
				var achievement = new Achievement();
				achievement.setID(results.getInt("ID"));
				achievement.setName(results.getString("Name"));
				achievement.setCompletion(results.getInt("Completion"));
				achievements.add(achievement);
			}
			return achievements;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
