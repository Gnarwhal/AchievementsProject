package achievements.controllers;

import achievements.services.ImageService;
import achievements.services.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/platform")
public class PlatformController {

	@Autowired
	private ImageService imageService;
	@Autowired
	private PlatformService platformService;

	@GetMapping(value = "/{platform}/image")
	public void getIcon(@PathVariable("platform") int platform, HttpServletResponse response) {
		var icon = platformService.getIcon(platform);
		imageService.send(icon, "platform", response);
	}
}
