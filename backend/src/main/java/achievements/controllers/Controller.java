package achievements.controllers;

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

	@RequestMapping(value = "/achievements", method = GET, produces = "application/json")
	public ResponseEntity<String> index() {
		try {
			var achievements = db.getAchievements();
			var mapper = new ObjectMapper();
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
}
