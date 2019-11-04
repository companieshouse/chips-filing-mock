package uk.gov.companieshouse;

import org.springframework.context.annotation.Bean;

import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

@org.springframework.context.annotation.Configuration
public class Configuration {
    
    @Bean
    SerializerFactory serializerFactory() {
        return new SerializerFactory();
    }

    @Bean
    DeserializerFactory deserializerFactory() {
        return new DeserializerFactory();
    }
}
