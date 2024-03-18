package cr.ac.backend.apigateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;


@SpringBootApplication(
        scanBasePackages = {
                "cr.ac.backend.apigateway",
                //"com.m4n0.amq"
        })

@RestController
@EnableDiscoveryClient

public class ApiGatewayServerApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ApiGatewayServerApplication.class, args);
    }


}
