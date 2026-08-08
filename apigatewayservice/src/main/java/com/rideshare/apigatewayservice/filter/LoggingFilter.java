package com.rideshare.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info(
                """

                ==========================================
                Incoming Request
                ==========================================
                Method : {}
                URI    : {}
                Host   : {}
                ==========================================

                """,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI(),
                exchange.getRequest()
                        .getHeaders()
                        .getHost()
        );
        log.info("Incoming Path: {}", exchange.getRequest().getURI().getPath());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

