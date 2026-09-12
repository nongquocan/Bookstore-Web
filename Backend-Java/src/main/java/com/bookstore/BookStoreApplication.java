package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class BookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║     🏪 BookStore Backend Started 🏪       ║");
        System.out.println("║  ✅ Server running on http://localhost:8080  ║");
        System.out.println("║  ✅ API: http://localhost:8080/api/books    ║");
        System.out.println("║  ✅ Health: http://localhost:8080/api/books/health ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
    }

    /**
     * Create RestTemplate bean for Google Books API calls
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
