package achievements.controllers;

import achievements.data.APError;
import achievements.data.Session;
import achievements.data.User;
import achievements.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

	@Autowired
	private AuthenticationService authService;

	/**
	 * Acceptable codes
	 *  0 => Success
	 *  1 => Email already registered
	 *
	 * -1 => Unknown error
	 */
	@PostMapping(value = "/create_user", consumes = "application/json", produces = "application/json")
	public ResponseEntity createUser(@RequestBody User user) {
		var response = authService.createUser(user);
		if (response.status == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(response.session);
		} else if (response.status > 0) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APError(response.status));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APError(response.status));
		}
	}

	/**
	 * DO NOT RETURN CODE DIRECTLY!
	 *
	 * User should only ever recieve -1, 0, or 1. The specific authentication error should be hidden.
	 *
	 * Acceptable codes
	 *  0 => Success
	 *  1 => Unregistered email address
	 *  2 => Incorrect password
	 *
	 * -1 => Unknown error
	 */
	@PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
	public ResponseEntity login(@RequestBody User user) {
		var response = authService.login(user);
		if (response.status == 0) {
			return ResponseEntity.ok(response.session);
		} else if (response.status > 0) {
			// Hardcoded 1 response code
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APError(1));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APError(response.status));
		}
	}

	@PostMapping(value = "/refresh", consumes = "application/json", produces = "application/json")
	public ResponseEntity refresh(@RequestBody Session key) {
		if (authService.refresh(key)) {
			return ResponseEntity.ok("{}");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{}");
		}
	}

	@PostMapping(value = "/logout", consumes = "application/json")
	public ResponseEntity logout(@RequestBody Session session) {
		authService.logout(session);
		return ResponseEntity.ok("{}");
	}
}
