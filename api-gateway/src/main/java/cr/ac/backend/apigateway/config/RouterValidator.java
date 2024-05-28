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
            "/workout/all",
            "/muscularGroup/all",
            "/workoutSpecification/all",
            "/dailyRoutine/all",
            "/workoutPlan/all"

    );

    public static final List<String> trainerRoutes = List.of(
            "/user/allByTrainer",
            "/user/all",
            "/user/id",
            "/user/save",
            "/user/update",
            "/workout/all",
            "/workout/save",
            "/workout/id",
            "/workout/update",
            "/workout/delete",
            "/muscularGroup/save",
            "/muscularGroup/all",
            "/muscularGroup/update",
            "/muscularGroup/delete",
            "/workoutSpecification/id",
            "/workoutSpecification/all",
            "/workoutSpecification/save",
            "/workoutSpecification/update",
            "/workoutSpecification/delete",
            "/workoutSpecification/allByUser",
            "/dailyRoutine/all",
            "/dailyRoutine/id",
            "/dailyRoutine/save",
            "/dailyRoutine/update",
            "/dailyRoutine/delete",
            "/workoutPlan/all",
            "/workoutPlan/id",
            "/workoutPlan/save",
            "/workoutPlan/update",
            "/workoutPlan/delete",
            "/workoutPlan/getTemplates"
    );

    public static final List<String> clientRoutes = List.of(
            "/workout/all",
            "/workoutSpecification/allByUser",
            "/dailyRoutine/all",
            "/workoutPlan/all",
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
            "/workout/all",
            "/workout/id",
            "/workout/save",
            "/workout/update",
            "/workout/delete",
            "/muscularGroup/save",
            "/muscularGroup/all",
            "/muscularGroup/id",
            "/muscularGroup/update",
            "/muscularGroup/delete",
            "/workoutSpecification/all",
            "/workoutSpecification/id",
            "/workoutSpecification/save",
            "/workoutSpecification/update",
            "/workoutSpecification/delete",
            "/workoutSpecification/allByUser",
            "/dailyRoutine/all",
            "/dailyRoutine/id",
            "/dailyRoutine/save",
            "/dailyRoutine/update",
            "/dailyRoutine/delete",
            "/workoutPlan/all",
            "/workoutPlan/id",
            "/workoutPlan/save",
            "/workoutPlan/update",
            "/workoutPlan/delete",
            "/workoutPlan/getTemplates"

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
