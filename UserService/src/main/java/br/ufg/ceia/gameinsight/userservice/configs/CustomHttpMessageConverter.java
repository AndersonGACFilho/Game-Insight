package br.ufg.fullstack.rpg_character_sheet_manager.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class CustomHttpMessageConverter {

    @Bean
    public MappingJackson2HttpMessageConverter
        mappingJackson2HttpMessageConverter(ObjectMapper objectMapper)
    {
        MappingJackson2HttpMessageConverter converter =
            new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(Collections.singletonList(
            new MediaType(
                "application",
                "json",
                StandardCharsets.UTF_8)));
        return converter;
    }
}
