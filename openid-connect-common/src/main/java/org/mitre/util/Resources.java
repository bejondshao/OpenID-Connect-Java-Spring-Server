package org.mitre.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by bejond on 8/5/18.
 */
@Component
public class Resources {

	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}
}
