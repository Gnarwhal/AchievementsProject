package achievements.controllers;

import achievements.data.APError;
import achievements.data.request.RateAchievement;
import achievements.services.ImageService;
import achievements.services.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/achievement")
public class AchievementController {

	@Autowired
	private AchievementService achievementService;
	@Autowired
	private ImageService imageService;

	@GetMapping(value = "/{achievement}", produces = "application/json")
	public ResponseEntity getAchievement(@PathVariable("achievement") int achievementId) {
		var achievement = achievementService.getAchievement(achievementId);
		if (achievement == null) {
			return ResponseEntity.badRequest().body(new APError(1, "Failed to get achievement"));
		} else {
			return ResponseEntity.ok(achievement);
		}
	}

	@GetMapping(value = "/{achievement}/image")
	public void getProfilePicture(@PathVariable("achievement") int achievement, HttpServletResponse response) {
		var icon = achievementService.getIcon(achievement);
		imageService.send(icon, "achievement", response);
	}

	@GetMapping(value = "/{achievement}/rating/{user}")
	public ResponseEntity getRating(@PathVariable("achievement") int achievement, @PathVariable("user") int user) {
		var rating = achievementService.getRating(achievement, user);
		if (rating == null) {
			return ResponseEntity.badRequest().body("{}");
		} else {
			return ResponseEntity.ok(rating);
		}
	}

	@PostMapping(value = "/{achievement}/rating/{user}")
	public ResponseEntity setRating(@PathVariable("achievement") int achievement, @PathVariable("user") int user, @RequestBody RateAchievement rating) {
		var review = achievementService.setRating(achievement, user, rating);
		if (review == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
		} else if (review.getSessionKey() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(review);
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body("{}");
		}
	}
}
