package achievements.controllers;

import achievements.data.APError;
import achievements.data.APPostRequest;
import achievements.data.query.AddPlatformRequest;
import achievements.data.query.RemovePlatformRequest;
import achievements.data.query.SetUsername;
import achievements.services.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

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
		var pfp = userService.getProfileImageType(user);
		if (pfp == null) {

		} else {
			var file = new File("images/user/" + pfp[0] + "." + pfp[1]);
			response.setContentType("image/" + pfp[2]);
			try {
				var stream = new FileInputStream(file);
				IOUtils.copy(stream, response.getOutputStream());

				response.flushBuffer();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@PostMapping(value = "/{user}/image", consumes = "multipart/form-data", produces = "application/json")
	public ResponseEntity setProfilePicture(@PathVariable("user") int user, @RequestPart APPostRequest session, @RequestPart MultipartFile file) {
		try {
			var type = userService.setProfileImageType(user, session.getKey(), file.getContentType());
			if ("not_an_image".equals(type)) {
				return ResponseEntity.badRequest().body("{ \"code\": 1, \"message\": \"Not an image type\" }");
			} else if ("unsupported_type".equals(type)) {
				return ResponseEntity.badRequest().body("{ \"code\": 1, \"message\": \"Unsupported file type\" }");
			} else if ("forbidden".equals(type)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{ \"code\": 2, \"message\": \"Invalid credentials\" }");
			} else if (!"unknown".equals(type)) {
				var pfp = new FileOutputStream("images/user/" + user + "." + type);
				FileCopyUtils.copy(file.getInputStream(), pfp);
				pfp.close();
				return ResponseEntity.status(HttpStatus.CREATED).body("{ \"code\": 0, \"message\": \"Success\" }");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"code\": -1, \"message\": \"Unknown error\" }");
	}

	@PostMapping(value = "/{user}/platforms/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity addPlatformForUser(@PathVariable("user") int userId, @RequestBody AddPlatformRequest request) {
		var result = userService.addPlatform(userId, request);
		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body("{}");
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}

	@PostMapping(value = "/{user}/platforms/remove", consumes = "application/json", produces = "application/json")
	public ResponseEntity removePlatformForUser(@PathVariable("user") int userId, @RequestBody RemovePlatformRequest request) {
		var result = userService.removePlatform(userId, request);
		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body("{}");
		} else {
			return ResponseEntity.badRequest().body("{}");
		}
	}
}
