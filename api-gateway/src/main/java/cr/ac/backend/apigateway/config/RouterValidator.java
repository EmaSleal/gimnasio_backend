package cr.ac.backend.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouterValidator {

    public static final List<String> publicRoutes = List.of(
            "/Login",
            "/forgot-password",
            "/exercise/all",
            "/muscularGroup/all",
            "/exerciseSpecified/all",
            "/rutineDay/all",
            "/rutine/all"

    );

    public static final List<String> trainerRoutes = List.of(
            "/user/allByTrainer",
            "/user/all",
            "/user/id",
            "/user/save",
            "/user/update",
            "/exercise/all",
            "/exercise/save",
            "/exercise/id",
            "/exercise/update",
            "/exercise/delete",
            "/muscularGroup/save",
            "/muscularGroup/all",
            "/muscularGroup/update",
            "/muscularGroup/delete",
            "/exerciseSpecified/id",
            "/exerciseSpecified/all",
            "/exerciseSpecified/save",
            "/exerciseSpecified/update",
            "/exerciseSpecified/delete",
            "/exerciseSpecified/allByUser",
            "/rutineDay/all",
            "/rutineDay/id",
            "/rutineDay/save",
            "/rutineDay/update",
            "/rutineDay/delete",
            "/rutine/all",
            "/rutine/id",
            "/rutine/save",
            "/rutine/update",
            "/rutine/delete"
    );

    public static final List<String> clientRoutes = List.of(
            "/exercise/all",
            "/exerciseSpecified/allByUser",
            "/rutineDay/all",
            "/rutine/all",
            "/user/update",
            "/user/id"
    );

    public static final List<String> adminRoutes = List.of(
            "/user/allByTrainer",
            "/user/all",
            "/user/id",
            "/user/save",
            "/user/update",
            "/user/delete",
            "/exercise/all",
            "/exercise/id",
            "/exercise/save",
            "/exercise/update",
            "/exercise/delete",
            "/muscularGroup/save",
            "/muscularGroup/all",
            "/muscularGroup/id",
            "/muscularGroup/update",
            "/muscularGroup/delete",
            "/exerciseSpecified/all",
            "/exerciseSpecified/id",
            "/exerciseSpecified/save",
            "/exerciseSpecified/update",
            "/exerciseSpecified/delete",
            "/exerciseSpecified/allByUser",
            "/rutineDay/all",
            "/rutineDay/id",
            "/rutineDay/save",
            "/rutineDay/update",
            "/rutineDay/delete",
            "/rutine/all",
            "/rutine/id",
            "/rutine/save",
            "/rutine/update",
            "/rutine/delete"

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
