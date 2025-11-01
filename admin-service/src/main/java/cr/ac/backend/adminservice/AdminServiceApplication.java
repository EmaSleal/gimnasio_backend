package cr.ac.backend.adminservice;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Boot Admin Server Application
 * 
 * This service provides a web-based UI for monitoring and managing Spring Boot applications.
 * It automatically discovers services registered with Eureka and displays their health,
 * metrics, logs, and other actuator endpoints.
 * 
 * Key Features:
 * - Centralized dashboard for all microservices
 * - Real-time health and metrics visualization
 * - Log viewing and JVM thread dump analysis
 * - Notification support for service down/up events
 * - Secured with Spring Security (basic auth)
 * 
 * Access: http://localhost:9000
 * Default credentials configured in application.yml
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
