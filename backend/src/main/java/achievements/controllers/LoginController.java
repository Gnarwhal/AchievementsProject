package achievements.controllers;

import achievements.data.User;
import achievements.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class LoginController {

	@Autowired
	private AuthenticationService authService;

	/**
	 * Acceptable codes
	 * 0 => Success
	 * 1 => Email already registered
	 *
	 * -1 => Unknown error
	 */
	@RequestMapping(value = "/create_user", method = POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity createUser(@RequestBody User user) {
		var response = authService.createUser(user);
		if (response.status == 0) {
			return ResponseEntity.ok("{ \"key\": \"" + authService.session().generate(response.id) + "\", \"id\": " + response.id + " }");
		} else if (response.status > 0) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{ \"code\": " + response.status + " }");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"code\": " + response.status + " }");
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
	public ResponseEntity login(@RequestParam(value = "guest", required = false) boolean guest, @RequestBody User user) {
		var response = guest ?
			authService.GUEST :
			authService.login(user);
		if (response.status == 0) {
			return ResponseEntity.ok("{ \"key\": \"" + authService.session().generate(response.id) + "\", \"id\": " + response.id + " }");
		} else if (response.status > 0) {
			// Hardcoded 1 response code
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{ \"code\": 1 }");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"code\": " + response.status + " }");
		}
	}
}
