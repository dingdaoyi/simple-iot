package com.github.dingdaoyi.config.base;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author dingyunwei
 */
@Configuration
public class WebCommonFilter {

	@Bean
	@ConditionalOnMissingBean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern("*");
		config.setAllowCredentials(true);
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");
		config.addExposedHeader("*");
		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		corsConfigurationSource.registerCorsConfiguration("/**", config);
		return new CorsFilter(corsConfigurationSource);
	}
}