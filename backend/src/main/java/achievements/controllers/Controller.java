package achievements.controllers;

import achievements.data.Achievements;
import achievements.data.User;
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
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class Controller {

	@Autowired
	private DbService db;

	public Controller() {}

	@RequestMapping(value = { "/achievements", "/achievements/{Name}" }, method = GET, produces = "application/json")
	public ResponseEntity<String> fetchAchievements(@PathVariable(value = "Name", required = false) String getName) {
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

	@RequestMapping(value = { "/games", "/games/{Name}" }, method = GET, produces = "application/json")
	public ResponseEntity<String> fetchGames(@PathVariable(value = "Name", required = false) String getName) {
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

	/**
	 * Acceptable codes
	 * 0 => Success
	 * 1 => Email already registered
	 *
	 * -1 => Unknown error
	 */
	@RequestMapping(value = "/create_user", method = POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity createUser(@RequestBody User user) {
		var status = db.createUser(user);
		if (status == 0) {
			return ResponseEntity.ok("{ \"key\": \"aoeuhtns\" }");
			//var sessionKey = db.generateSessionKey(user);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{ \"code\": " + status + " }");
		}
	}

	/**
	 * DO NOT RETURN CODE DIRECTLY!
	 *
	 * User should only ever recieve -1, 0, or 1. The specific authentication error should be hidden.
	 *
	 * Acceptable codes
	 * 0 => Success
	 * 1 => Unregistered email address
	 * 2 => Incorrect password
	 *
	 * -1 => Unknown error
	 */
	@RequestMapping(value = "/login", method = POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity login(@RequestBody User user) {
		var status = db.login(user);
		if (status == 0) {
			return ResponseEntity.ok("{ \"key\": \"aoeuhtns\" }");
		} else if (status > 0) {
			// Hardcoded 1 response code
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{ \"code\": 1 }");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"code\": " + status + " }");
		}
	}
}
