package achievements.services;

import achievements.data.Session;
import achievements.data.User;
import achievements.misc.DbConnection;
import achievements.misc.Password;
import achievements.misc.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.*;

@Service
public class AuthenticationService {

	public static class LoginResponse {
		public int     status;
		public Session session;

		public LoginResponse() {
			this.status = 0;
		}

		public LoginResponse(int status) {
			this.status  = status;
			this.session = null;
		}

		public LoginResponse(int status, Session session) {
			this.status  = status;
			this.session = session;
		}
	}

	@Autowired
	private DbConnection dbs;
	private Connection db;

	@Autowired
	private SessionManager session;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	public LoginResponse createUser(User user) {
		if (!user.getEmail().matches(".+@\\w+\\.\\w+")) {
			return new LoginResponse(2);
		}

		try {
			var statement = db.prepareCall("{? = call CreateUser(?, ?, ?, ?, ?, ?)}");
			statement.registerOutParameter(1, Types.INTEGER);
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getUsername());

			var password = Password.generate(user.getPassword());
			statement.setString(4, password.salt);
			statement.setString(5, password.hash);

			statement.registerOutParameter(6, Types.INTEGER);
			statement.registerOutParameter(7, Types.INTEGER);

			statement.execute();
			var response = new LoginResponse(
				statement.getInt(1),
				session.generate(
					statement.getInt(6),
					statement.getInt(7),
					false
				)
			);
			statement.close();

			return response;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new LoginResponse(-1);
	}

	public LoginResponse login(User user) {
		var response = new LoginResponse(-1);
		try {
			var statement = db.prepareCall("{? = call GetUserLogin(?)}");
			statement.registerOutParameter(1, Types.INTEGER);
			statement.setString(2, user.email);

			statement.execute();
			if (statement.getInt(1) == 0) {
				var result = statement.executeQuery();
				result.next();
				var salt = result.getString("Salt");
				var hash = result.getString("Password");
				if (Password.validate(salt, user.getPassword(), hash)) {
					response = new LoginResponse(
						0,
						session.generate(
							result.getInt("ID"),
							result.getInt("Hue"),
							result.getBoolean("Admin")
						)
					);
				} else {
					response = new LoginResponse(2);
				}
			} else {
				response = new LoginResponse(1);
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return response;
	}

	public boolean refresh(Session key) { return session.refresh(key.getKey()); }

	public boolean openAuth() {
		try {
			var stmt = db.prepareCall("{call HasUser(?)}");
			stmt.registerOutParameter(1, Types.BOOLEAN);

			stmt.execute();
			return !stmt.getBoolean(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void logout(Session key) {
		session.remove(key.getKey());
	}

	public SessionManager session() {
		return session;
	}
}
