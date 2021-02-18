package achievements;

import achievements.misc.DbConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		var context = SpringApplication.run(Application.class, args);

		// Verify the database connection succeeded
		var db = context.getBean(DbConnection.class);
		if (db.getConnection() == null) {
			SpringApplication.exit(context, () -> 0);
		}
	}

	@Bean
	public WebMvcConfigurer configurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
			registry
				.addMapping("/**")
				.allowedOrigins("*");
			}
		};
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}