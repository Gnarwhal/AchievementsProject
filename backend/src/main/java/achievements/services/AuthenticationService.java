package achievements.services;

import achievements.data.User;
import achievements.misc.DbConnectionService;
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
		public Integer id;

		public LoginResponse() {
			this.status = 0;
			this.id     = null;
		}

		public LoginResponse(int status) {
			this.status = status;
			this.id     = null;
		}

		public LoginResponse(int status, int id) {
			this.status = status;
			this.id     = id;
		}
	}

	public static final LoginResponse GUEST = new LoginResponse();

	@Autowired
	private DbConnectionService dbs;
	private Connection db;

	private SessionManager session;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
		session = new SessionManager();
	}

	public LoginResponse createUser(User user) {
		if (!user.getEmail().matches(".+@\\w+\\.\\w+")) {
			return new LoginResponse(2);
		}

		try {
			var statement = db.prepareCall("{? = call CreateUser(?, ?, ?, ?, ?)}");
			statement.registerOutParameter(1, Types.INTEGER);
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getUsername());

			var password = Password.generate(user.getPassword());
			statement.setString(4, password.salt);
			statement.setString(5, password.hash);

			statement.registerOutParameter(6, Types.INTEGER);

			statement.execute();
			var response = new LoginResponse(statement.getInt(1), statement.getInt(6));
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
			var statement = db.prepareCall("{call GetUserLogin(?)}");
			statement.setString(1, user.email);

			var result = statement.executeQuery();
			if (result.next()) {
				var salt = result.getString("Salt");
				var hash = result.getString("Password");
				if (Password.validate(salt, user.getPassword(), hash)) {
					response = new LoginResponse(0, result.getInt("ID"));
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

	public SessionManager session() {
		return session;
	}
}
