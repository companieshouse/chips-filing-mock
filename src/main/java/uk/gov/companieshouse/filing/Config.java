package uk.gov.companieshouse.filing;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;

@Configuration
public class Config {

    @Bean
    DeserializerFactory deserializerFactory() {
        return new DeserializerFactory();
    }
    
    @Bean
    String applicationName() {
        return Application.APPLICATION_NAME;
    }
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
