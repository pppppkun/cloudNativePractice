package nju.cloud.prac;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PracApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracApplication.class, args);
        System.out.println("WHY");
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("S{spring.application.name}") String applicationName){
        return (registry -> registry.config().commonTags("application", applicationName));
    }

}
