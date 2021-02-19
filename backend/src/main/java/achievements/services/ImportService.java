package achievements.services;

import achievements.data.importing.ImportPlatform;
import achievements.data.importing.ImportUser;
import achievements.data.importing.ImportUserPlatform;
import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Service
public class ImportService {

	@Autowired
	private DbConnection dbs;
	private Connection   db;

	@Autowired
	private AuthenticationService authService;
	@Autowired
	private UserService userService;

	@PostConstruct
	public void init() {
		db = dbs.getConnection();
	}

	public int importPlatform(ImportPlatform platform) {
		if (authService.session().validateAdmin(platform.getUserId(), platform.getSessionKey())) {
			try {
				var stmt = db.prepareCall("{call AddPlatform(?, ?)}");
				stmt.setString(1, platform.getName());
				stmt.registerOutParameter(2, Types.INTEGER);

				stmt.execute();
				return 0;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public int importUser(ImportUser user) {
		if (authService.session().validateAdmin(user.getUserId(), user.getSessionKey())) {
			try {
				var response = authService.createUser(user);
				if (user.isAdmin()) {
					var stmt = db.prepareCall("{call OpUser(?)}");
					stmt.setInt(1, response.session.getId());
					stmt.execute();
				}

				return 0;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public int importUserPlatform(ImportUserPlatform userPlatform) {
		if (authService.session().validateAdmin(userPlatform.getUserId(), userPlatform.getSessionKey())) {
			try {
				var stmt = db.prepareCall("{call GetIdFromEmail(?, ?)}");
				stmt.setString(1, userPlatform.getUserEmail());
				stmt.registerOutParameter(2, Types.INTEGER);

				stmt.execute();
				return userService.addPlatform(stmt.getInt(2), userPlatform, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
}
