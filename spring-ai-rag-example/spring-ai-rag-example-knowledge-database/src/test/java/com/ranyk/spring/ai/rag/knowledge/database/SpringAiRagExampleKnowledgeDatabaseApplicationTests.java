package com.ranyk.spring.ai.rag.knowledge.database;

import com.ranyk.spring.ai.rag.tool.ai.tools.WeatherForLocationToolFunction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@SpringBootTest
class SpringAiRagExampleKnowledgeDatabaseApplicationTests {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("天气查询工具测试")
	void weatherForLocationToolTest() {
		String result = new WeatherForLocationToolFunction(objectMapper).getWeatherForLocation("成都");
		log.info("天气查询结果: {}", result);
	}

}
