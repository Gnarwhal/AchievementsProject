package achievements.controllers;

import achievements.services.ImageService;
import achievements.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/game")
public class GameController {

	@Autowired
	private GameService gameService;
	@Autowired
	private ImageService imageService;

	@GetMapping(value = "/{game}/image")
	public void getProfilePicture(@PathVariable("game") int game, HttpServletResponse response) {
		var icon = gameService.getIcon(game);
		imageService.send(icon, "game", response);
	}
}
