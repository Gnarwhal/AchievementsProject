package achievements.controllers;

import achievements.data.query.SearchAchievements;
import achievements.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

	@Autowired
	private SearchService searchService;

	@PostMapping(value = "/achievements", consumes = "application/json", produces = "application/json")
	public ResponseEntity searchAchievements(@RequestBody SearchAchievements searchAchievements) {
		var achievements = searchService.searchAchievements(searchAchievements);
		if (achievements != null) {
			return ResponseEntity.ok(achievements);
		} else {
			return ResponseEntity.badRequest().body("[]");
		}
	}
}
