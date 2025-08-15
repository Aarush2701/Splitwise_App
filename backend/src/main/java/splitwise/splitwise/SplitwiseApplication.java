package splitwise.splitwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class SplitwiseApplication {

	public static void main(String[] args) {

		SpringApplication.run(SplitwiseApplication.class, args);
	}

}


