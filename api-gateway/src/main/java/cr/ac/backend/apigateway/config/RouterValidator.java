package cr.ac.backend.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouterValidator {

    public static final List<String> publicRoutes = List.of(
            "/Login",
            "/forgot-password"
    );

    public static final List<String> trainerRoutes = List.of(
            "/user/allByTrainer",
            "/user/all",
            "/trainer/add",
            "/trainer/update",
            "/trainer/delete"
    );

    public static final List<String> clientRoutes = List.of(
            "/client/all",
            "/client/add",
            "/client/update",
            "/client/delete"
    );

    public static final List<String> adminRoutes = List.of(
            "/user/all",
            "/admin/all",
            "/admin/add",
            "/admin/update",
            "/admin/delete"

    );



    public Predicate<ServerHttpRequest> isSecured = request -> publicRoutes
            .stream()
            .noneMatch(uri -> request.getURI().getPath().contains(uri));

    //en trainer utilizo publicRoutes, clientRoutes y trainerRoutes
    public Predicate<ServerHttpRequest> isTrainer = request -> trainerRoutes
            .stream()
            .anyMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isClient = request -> clientRoutes
            .stream()
            .anyMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> isAdmin = request -> adminRoutes
            .stream()
            .anyMatch(uri -> request.getURI().getPath().contains(uri));
}
