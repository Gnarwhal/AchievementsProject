package achievements;

import achievements.services.DbConnectionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		var context = SpringApplication.run(Application.class, args);

		// Verify the database connection succeeded
		var db = context.getBean(DbConnectionService.class);
		if (db.getConnection() == null) {
			SpringApplication.exit(context, () -> 0);
		}
	}
}