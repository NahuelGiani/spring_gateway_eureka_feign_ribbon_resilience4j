package com.nahuel.filters.factory;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.nahuel.configurations.GatewayFilterConfiguration;

import reactor.core.publisher.Mono;

@Component
public class TestGatewayFilterFactory extends AbstractGatewayFilterFactory<GatewayFilterConfiguration> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestGatewayFilterFactory.class);

	public TestGatewayFilterFactory() {
		super(GatewayFilterConfiguration.class);
	}

	@Override
	public String name() {
		return "Test"; // Por defecto toma el sufijo de esta clase. Es decir, Test. No hace falta sobreescribirlo si no le cambio el nombre
					   // esta vez lo hice solo para entenderlo.
	}

	@Override
	public GatewayFilter apply(GatewayFilterConfiguration config) {
		return (exchange, chain) -> {
			LOGGER.info("Executing pre filter" + config.getMessage());
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				if(Optional.ofNullable(config.getCookieValue()).isPresent()) {
					exchange.getResponse().addCookie(ResponseCookie.from(config.getCookieKey(), config.getCookieValue()).build());
				}
				LOGGER.info("Executing post filter" + config.getMessage());
			}));
		};
	}

}
