package achievements.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

@Component
public class DbConnectionService {

	private Connection connection;

	@Value("${database.server}")
	private String serverName;
	@Value("${database.name}")
	private String databaseName;
	@Value("${database.user.name}")
	private String username;
	@Value("${database.user.password}")
	private String password;

	public DbConnectionService() {}

	@PostConstruct
	public void connect() {
		try {
			var dataSource = new SQLServerDataSource();
			dataSource.setServerName  (serverName  );
			dataSource.setDatabaseName(databaseName);
			dataSource.setUser        (username    );
			dataSource.setPassword    (password    );
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return this.connection;
	}

	@PreDestroy
	public void disconnect() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
