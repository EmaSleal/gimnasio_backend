package cr.ac.backend.apigateway;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;



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
