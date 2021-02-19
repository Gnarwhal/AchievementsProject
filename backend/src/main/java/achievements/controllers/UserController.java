package achievements.controllers;

import achievements.data.APError;
import achievements.data.APPostRequest;
import achievements.data.request.AddPlatform;
import achievements.data.request.RemovePlatform;
import achievements.data.request.SetUsername;
import achievements.services.ImageService;
import achievements.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ImageService imageService;

	@GetMapping(value = "/{user}", produces = "application/json")
	public ResponseEntity getProfile(@PathVariable("user") int user) {
		var profile = userService.getProfile(user);
		if (profile == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APError(1, "Failed to get user profile"));
		} else {
			return ResponseEntity.ok(profile);
		}
	}

	@PostMapping(value = "/{user}/username", consumes = "application/json", produces = "application/json")
	public ResponseEntity setUsername(@PathVariable("user") int userId, @RequestBody SetUsername username) {
		var name = userService.setUsername(userId, username);
		if (name == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body("{}");
		}
		return ResponseEntity.badRequest().body("{}");
	}

	@GetMapping(value = "/{user}/image")
	public void getProfilePicture(@PathVariable("user") int user, HttpServletResponse response) {
		var profileImage = userService.getProfileImage(user);
		imageService.send(profileImage, "user", response);
	}

	@PostMapping(value = "/{user}/image", consumes = "multipart/form-data", produces = "application/json")
	public ResponseEntity setProfilePicture(@PathVariable("user") int user, @RequestPart APPostRequest session, @RequestPart MultipartFile file) {
		try {
			var type = userService.setProfileImage(user, session.getKey(), file);
			if ("not_an_image".equals(type)) {
				return ResponseEntity.badRequest().body("{ \"code\": 1, \"message\": \"Not an image type\" }");
			} else if ("unsupported_type".equals(type)) {
				return ResponseEntity.badRequest().body("{ \"code\": 1, \"message\": \"Unsupported file type\" }");
			} else if ("forbidden".equals(type)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{ \"code\": 2, \"message\": \"Invalid credentials\" }");
			} else if ("success".equals(type)) {
				return ResponseEntity.status(HttpStatus.CREATED).body("{ \"code\": 0, \"message\": \"Success\" }");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"code\": -1, \"message\": \"Unknown error\" }");
	}

	@PostMapping(value = "/{user}/platforms/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity addPlatformForUser(@PathVariable("user") int userId, @RequestBody AddPlatform request) {
		var result = userService.addPlatform(userId, request, true);
		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body("{}");
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}

	@PostMapping(value = "/{user}/platforms/remove", consumes = "application/json", produces = "application/json")
	public ResponseEntity removePlatformForUser(@PathVariable("user") int userId, @RequestBody RemovePlatform request) {
		var result = userService.removePlatform(userId, request);
		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body("{}");
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}

	@GetMapping(value = "/{user}/noteworthy", produces = "application/json")
	public ResponseEntity getNoteworthy(@PathVariable("user") int userId) {
		var result = userService.getNoteworthy(userId);
		if (result != null) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}
}
