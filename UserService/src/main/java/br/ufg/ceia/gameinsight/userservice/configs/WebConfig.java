package br.ufg.fullstack.rpg_character_sheet_manager.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;

/**
 * This class represents the web configuration.
 * <p>
 * This class is responsible for configuring the web.
 */
@Configuration
public class WebConfig {

    /**
     * Configures the string http message converter.
     *
     * @return The string http message converter.
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }
}
