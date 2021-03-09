package com.tk.fluentGraph;

import io.jsondb.JsonDBOperations;
import io.jsondb.JsonDBTemplate;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class FluentGraphApplication
{
    @Value("${jsondb.files}")
    private String dbFilesLocation;

    public static void main(String[] args)
    {
        SpringApplication.run(FluentGraphApplication.class, args);
    }

    @Bean
    public JsonDBOperations jsonDBOperations()
    {
        final String baseScanPackage = "com.tk.fluentGraph.model";

        return new JsonDBTemplate(dbFilesLocation, baseScanPackage);
    }

    @Bean
    public MapperFacade mapperFacade()
    {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        return mapperFactory.getMapperFacade();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer()
        {
            @Override
            public void addCorsMappings(CorsRegistry registry)
            {
                registry
                    .addMapping("/**")
                    .allowedOrigins("http://localhost:8080", "http://localhost:63342")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "OPTIONS", "DELETE");
            }
        };
    }
}
