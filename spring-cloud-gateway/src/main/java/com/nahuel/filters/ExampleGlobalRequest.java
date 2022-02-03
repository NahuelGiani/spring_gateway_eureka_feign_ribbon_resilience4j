package com.nahuel.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class ExampleGlobalRequest implements GlobalFilter, Ordered {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleGlobalRequest.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		LOGGER.info("Executing prefilter");
		exchange.getRequest().mutate().headers(h -> h.add("token", "12345"));
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			LOGGER.info("Executing postfilter");

			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(token -> {
				exchange.getResponse().getHeaders().add("token", token);
			});

			exchange.getResponse().getCookies().add("Color", ResponseCookie.from("Color", "Rojo").build());
			// exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
		})); // --> chain.filter(exchange) Con esto continuamos los filtros de la ejecución
				// hasta enviar el request . Cuando todos hayan terminado, el
				// then() finaliza y procesa un post request
	}

	@Override
	public int getOrder() {
		return 1;
	}

}
