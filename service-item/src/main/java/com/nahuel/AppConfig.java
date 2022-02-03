package com.nahuel;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class AppConfig {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	
//	El application.yml tiene prioridad por sobre esto que hicimos acá
//  Las dos maneras de configurar son validas. Por application parece más fácil
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer(){
		return factory -> factory.configureDefault(id -> {
			return new Resilience4JConfigBuilder(id)
					.circuitBreakerConfig(CircuitBreakerConfig.custom()
							.slidingWindowSize(10) // Esto es valor total
							.failureRateThreshold(50) // Esto es porcentaje 
							.waitDurationInOpenState(Duration.ofSeconds(10L)) // Tiempo que tarda en volver a cerrado el circuit breaker
							.permittedNumberOfCallsInHalfOpenState(5) // Esta es la cantidad de llamadas toleradas en estado semi abierto. Se reduce porque todavía no sabés si se arregló
							.slowCallRateThreshold(50) // Es el porcentaje del umbral de llamadas lentas. Gralmente es igual al failureRateThreshold
							.slowCallDurationThreshold(Duration.ofSeconds(2L)) // Es el tiempo que tiene que pasar para considerar a una llamada como "lenta"
							.build())
				//	.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2L)).build()) // Configurar timeout personalizado. Por defecto es 1, lo pasamos a 2 acá
					.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(6L)).build()) // Acá lo pongo en 6 para probar las "llamadas lentas"	
					.build();
		});
	}
	
}
