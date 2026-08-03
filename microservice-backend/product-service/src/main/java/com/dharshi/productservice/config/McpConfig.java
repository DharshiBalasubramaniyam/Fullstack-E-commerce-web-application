package com.dharshi.productservice.config;

import com.dharshi.productservice.services.McpService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    ToolCallbackProvider productTools(McpService mcpService) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(mcpService)
                .build();
    }

}
