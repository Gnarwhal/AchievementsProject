package achievements.services;

import achievements.misc.DbConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;

@Service
public class PlatformService {

	@Autowired
	private DbConnection dbs;
	private Connection   db;

	@PostConstruct
	private void init() {
		db = dbs.getConnection();
	}
}
