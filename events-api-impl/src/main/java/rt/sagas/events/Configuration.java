package rt.sagas.events;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;

@SpringBootConfiguration
@EnableJms
@EnableJpaRepositories
public class Configuration {
}
