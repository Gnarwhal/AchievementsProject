package achievements.controllers;

import achievements.data.importing.ImportPlatform;
import achievements.data.importing.ImportUser;
import achievements.data.importing.ImportUserPlatform;
import achievements.services.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/import")
public class ImportController {

	@Autowired
	private ImportService importService;

	@PostMapping(value = "/platform", consumes = "application/json", produces = "application/json")
	public ResponseEntity createPlatform(@RequestBody ImportPlatform platform) {
		var response = importService.importPlatform(platform);
		if (response == 0) {
			return ResponseEntity.ok("{}");
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}

	@PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
	public ResponseEntity createUser(@RequestBody ImportUser user) {
		var response = importService.importUser(user);
		if (response == 0) {
			return ResponseEntity.ok("{}");
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}

	@PostMapping(value = "/user/platform", consumes = "application/json", produces = "application/json")
	public ResponseEntity addUserToPlatform(@RequestBody ImportUserPlatform userPlatform) {
		var response = importService.importUserPlatform(userPlatform);
		if (response == 0) {
			return ResponseEntity.ok("{}");
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}
}
