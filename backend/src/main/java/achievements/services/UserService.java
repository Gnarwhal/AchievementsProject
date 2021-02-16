package achievements.services;

import achievements.data.Profile;
import achievements.data.query.AddPlatformRequest;
import achievements.data.query.RemovePlatformRequest;
import achievements.data.query.SetUsername;
import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class UserService {

	@Autowired
	private DbConnection dbs;
	private Connection db;

	@Autowired
	private AuthenticationService auth;

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

	private static final HashMap<String, String> VALID_IMAGE_TYPES = new HashMap<>();
	static {
		VALID_IMAGE_TYPES.put("apng",    "apng");
		VALID_IMAGE_TYPES.put("avif",    "avif");
		VALID_IMAGE_TYPES.put("gif",     "gif" );
		VALID_IMAGE_TYPES.put("jpeg",    "jpg" );
		VALID_IMAGE_TYPES.put("png",     "png" );
		VALID_IMAGE_TYPES.put("svg+xml", "svg" );
		VALID_IMAGE_TYPES.put("webp",    "webp");
	}
	public String[] getProfileImageType(int userId) {
		try {
			var stmt = db.prepareCall("{call GetUserImage(?)}");
			stmt.setInt(1, userId);

			var result = stmt.executeQuery();
			if (result.next()) {
				var type = result.getString("PFP");
				if (type == null) {
					return new String[] { "default", "png", "png" };
				} else {
					return new String[] { Integer.toString(userId), VALID_IMAGE_TYPES.get(type), type };
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String setProfileImageType(int userId, String sessionKey, String type) {
		try {
			if (type.matches("image/.*")) {
				type = type.substring(6);
				var extension = VALID_IMAGE_TYPES.get(type);
				if (!auth.session().validate(userId, sessionKey)) {
					return "forbidden";
				} else if (extension == null) {
					return "unsupported_type";
				} else {
					var stmt = db.prepareCall("{call SetUserImage(?, ?)}");
					stmt.setInt(1, userId);
					stmt.setString(2, type);

					stmt.execute();

					return extension;
				}
			} else {
				return "not_an_image";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "unknown";
	}

	public int addPlatform(int userId, AddPlatformRequest request) {
		try {
			if (auth.session().validate(userId, request.getSessionKey())) {
				var stmt = db.prepareCall("{call AddPlatform(?, ?, ?)}");
				stmt.setInt(1, userId);
				stmt.setInt(2, request.getPlatformId());
				stmt.setString(3, request.getPlatformUserId());

				stmt.execute();

				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int removePlatform(int userId, RemovePlatformRequest request) {
		try {
			if (auth.session().validate(userId, request.getSessionKey())) {
				var stmt = db.prepareCall("{call RemovePlatform(?, ?)}");
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
}
