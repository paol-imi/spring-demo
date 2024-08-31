package com.example.library.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 */
@Configuration
public class ModelMapperConfig {
	/**
	 * Creates a new ModelMapper bean.
	 *
	 * @return the ModelMapper bean
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}