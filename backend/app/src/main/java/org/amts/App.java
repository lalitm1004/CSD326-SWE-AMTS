package org.amts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "org.amts.infrastructure", "org.amts.adapters" })
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
