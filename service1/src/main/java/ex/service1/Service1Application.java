package ex.service1;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@Configuration
public class Service1Application {


	@Bean
	public RestClient client(){

		return RestClient.create();
	}

	public static void main(String[] args) {

		SpringApplication.run(Service1Application.class, args);
	}

}
