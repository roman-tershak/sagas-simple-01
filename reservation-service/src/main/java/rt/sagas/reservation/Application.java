package rt.sagas.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJpaRepositories
@EnableJms
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
