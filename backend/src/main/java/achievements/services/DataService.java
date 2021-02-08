package achievements.services;

import achievements.data.Achievement;
import achievements.data.Game;
import achievements.data.Profile;
import achievements.misc.DbConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.util.List;

@Service
public class DataService {

	@Autowired
	private DbConnectionService dbs;
	private Connection db;

	@Autowired
	private AuthenticationService auth;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}

	/*public List<Achievement> getUsers() {

	}

	public List<Game> getGames() {

	}

	public List<Profile> getProfiles() {

	}*/
}
