package achievements.controllers;

import achievements.services.ImageService;
import achievements.services.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/achievement")
public class AchievementController {

	@Autowired
	private AchievementService achievementService;
	@Autowired
	private ImageService imageService;

	@GetMapping(value = "/{achievement}/image")
	public void getProfilePicture(@PathVariable("achievement") int achievement, HttpServletResponse response) {
		var icon = achievementService.getIcon(achievement);
		imageService.send(icon, "achievement", response);
	}
}
