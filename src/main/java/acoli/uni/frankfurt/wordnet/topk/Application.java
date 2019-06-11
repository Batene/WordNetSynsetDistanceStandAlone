package acoli.uni.frankfurt.wordnet.topk;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		

	}

}
