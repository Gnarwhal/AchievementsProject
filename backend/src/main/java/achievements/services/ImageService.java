package achievements.services;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;

@Service
public class ImageService {
	public static final HashMap<String, String> MIME_TO_EXT = new HashMap<>();
	public static final HashMap<String, String> EXT_TO_MIME = new HashMap<>();
	static {
		MIME_TO_EXT.put("apng",    "apng");
		MIME_TO_EXT.put("avif",    "avif");
		MIME_TO_EXT.put("gif",     "gif" );
		MIME_TO_EXT.put("jpeg",    "jpg" );
		MIME_TO_EXT.put("png",     "png" );
		MIME_TO_EXT.put("svg+xml", "svg" );
		MIME_TO_EXT.put("webp",    "webp");

		EXT_TO_MIME.put("apng", "apng"   );
		EXT_TO_MIME.put("avif", "avif"   );
		EXT_TO_MIME.put("gif",  "gif"    );
		EXT_TO_MIME.put("jpg",  "jpeg"   );
		EXT_TO_MIME.put("png",  "png"    );
		EXT_TO_MIME.put("svg",  "svg+xml");
		EXT_TO_MIME.put("webp", "webp"   );
	}

	public String[] getImageType(CallableStatement stmt, int id) {
		try {
			stmt.setInt(1, id);

			var result = stmt.executeQuery();
			if (result.next()) {
				var type = result.getString(1);
				if (type != null) {
					return new String[] { id + "." + type, EXT_TO_MIME.get(type) };
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void send(String[] image, String type, HttpServletResponse response) {
		var file     = (File) null;
		var mimeType = (String) null;
		if (image == null) {
			file = new File("storage/images/default/" + type + ".png");
			mimeType = "png";
		} else {
			file = new File("storage/images/" + type + "/" + image[0]);
			mimeType = image[1];
		}
		try {
			var stream = new FileInputStream(file);
			IOUtils.copy(stream, response.getOutputStream());

			response.setStatus(200);
			response.setContentType("image/" + mimeType);
			response.flushBuffer();
			stream.close();

			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setStatus(500);
	}
}
