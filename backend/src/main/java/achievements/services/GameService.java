package achievements.services;

import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;

@Service
public class GameService {

	@Autowired
	private DbConnection dbs;
	private Connection   db;

	@Autowired
	private ImageService imageService;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	public String[] getIcon(int gameId) {
		try {
			var stmt = db.prepareCall("{call GetGameIcon(?)}");
			return imageService.getImageType(stmt, gameId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
