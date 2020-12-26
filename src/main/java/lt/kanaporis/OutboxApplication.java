package lt.kanaporis;

import lt.kanaporis.documents.DocumentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class OutboxApplication {
    public static void main(String[] args) {
        SpringApplication.run(OutboxApplication.class, args);
    }

    @Bean
    public CountDownLatch documentLatch() {
        return new CountDownLatch(1);
    }

    @Bean
    public CommandLineRunner run(DocumentService documentService, ConfigurableApplicationContext ctx) {
        return args -> {
            documentService.create();
            documentLatch().await(60, TimeUnit.SECONDS);
            SpringApplication.exit(ctx);
        };
    }
}
