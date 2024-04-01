package cr.ac.backend.apigateway.config;

import cr.ac.backend.apigateway.services.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request =  exchange.getRequest();
        log.info("Request path: {}", request.getPath());


        if (routerValidator.isSecured.test(request)) {
            log.info("request is secure");
            if (authMissing(request)) {
                return onError(exchange,  HttpStatus.UNAUTHORIZED);
            }
            String token = request.getHeaders().getOrEmpty("Authorization").get(0).split(" ")[1];

            if (jwtUtils.isTokenExpired(token)) {
                log.info("Token is expired");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String role = jwtUtils.extractRole(token);

            if (routerValidator.isTrainer.test(request) && (!role.equals("TRAINER") && !role.equals("ADMIN"))) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }else if (routerValidator.isClient.test(request) && (!role.equals("CLIENT") && !role.equals("ADMIN")) && !role.equals("TRAINER")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }else if (routerValidator.isAdmin.test(request) && !role.equals("ADMIN")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            } else {
                log.info("Request is authorized");
            }
        }
        log.info("end of filter");
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response =  exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.info("Response status: {}", response.getStatusCode());
        return response.setComplete();
    }


    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }


}
