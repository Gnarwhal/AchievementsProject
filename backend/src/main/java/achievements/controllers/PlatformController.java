package achievements.controllers;

import achievements.services.PlatformService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class PlatformController {

	@Autowired
	private PlatformService platforms;

	@GetMapping(value = "/platform/image/{id}", produces = "application/json")
	public void getPlatformImage(@PathVariable("id") int id, HttpServletResponse response) {
		try {
			var file = new File("images/platform/" + id + ".png");
			if (file.exists()) {
				var stream = new FileInputStream(file);
				IOUtils.copy(stream, response.getOutputStream());

				response.setContentType("image/png");
				response.setStatus(200);
				response.flushBuffer();
				stream.close();
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}
