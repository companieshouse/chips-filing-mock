package uk.gov.companieshouse.filing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

@Configuration
public class Config {
    
    @Bean
    SerializerFactory serializerFactory() {
        return new SerializerFactory();
    }

    @Bean
    DeserializerFactory deserializerFactory() {
        return new DeserializerFactory();
    }
}
