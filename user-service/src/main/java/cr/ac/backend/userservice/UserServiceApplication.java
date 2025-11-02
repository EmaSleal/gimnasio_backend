package cr.ac.backend.userservice;

import cr.ac.backend.userservice.model.User;
import cr.ac.backend.userservice.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(
        scanBasePackages = {
                "cr.ac.backend.userservice",
                //"com.m4n0.amq"
        }
)
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class,args);
    }

    //creo un usuario por defecto, comentar después de la primera ejecucion
    // @Bean
    // CommandLineRunner run(UserService service) {
    //     return args -> {
    //         service.register(new User(null,"sotoleal","sotoleal123","manusl2908@gmail.com", User.Rol.ADMIN,true,true,true,true,null,null,null,null));
    //         service.register(new User(null,"sotoleal123","sotoleal123","manusl29@gmail.com", User.Rol.ADMIN,true,true,true,true,1L,null,null,null));
    //     };
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}