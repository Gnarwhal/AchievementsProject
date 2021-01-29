package achievements.controllers;

import achievements.data.Achievements;
import achievements.data.Games;
import achievements.data.InternalError;
import achievements.services.DbService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class Controller {

	@Autowired
	private DbService db;

	public Controller() {}

	@RequestMapping(value = "/achievements/{Name}", method = GET, produces = "application/json")
	public ResponseEntity<String> fetchAchievements(@PathVariable("Name") String getName) {
		var achievements = (Achievements) null;
		if (getName == null) {
			achievements = db.getAchievements("%");
		} else {
			achievements = db.getAchievements(getName);
		}
		var mapper = new ObjectMapper();
		try {
			if (achievements == null) {
				return new ResponseEntity(mapper.writeValueAsString(new InternalError("Could not get achievements from database")), HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity(mapper.writeValueAsString(achievements), HttpStatus.OK);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ResponseEntity("{}", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/games/{Name}", method = GET, produces = "text/html")
	public ResponseEntity<String> fetchGames(@PathVariable("Name") String getName) {
		var games = (Games) null;
		if (getName == null) {
			games = db.getGames("%");
		} else {
			games = db.getGames(getName);
		}
		var mapper = new ObjectMapper();
		try {
			if (games == null) {
				return new ResponseEntity(mapper.writeValueAsString(new InternalError("Could not get games from database")), HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity(mapper.writeValueAsString(games), HttpStatus.OK);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ResponseEntity("{}", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
