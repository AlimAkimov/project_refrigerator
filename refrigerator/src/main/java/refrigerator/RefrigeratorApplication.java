package refrigerator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class RefrigeratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefrigeratorApplication.class, args);
	}

}
