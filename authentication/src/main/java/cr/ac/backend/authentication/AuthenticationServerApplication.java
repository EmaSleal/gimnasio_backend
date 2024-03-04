package cr.ac.backend.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */

@SpringBootApplication(
        scanBasePackages = {
                "cr.ac.backend.authentication",
                //"com.m4n0.amq"
        }
)
/*@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:application-${spring.profiles.active}.properties")
})*/
@EnableDiscoveryClient
public class AuthenticationServerApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(AuthenticationServerApplication.class,args);
    }
}

