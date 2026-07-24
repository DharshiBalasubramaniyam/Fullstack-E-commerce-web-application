package com.dharshi.cartservice.config;


import com.dharshi.cartservice.services.McpService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    ToolCallbackProvider cartTools(McpService mcpService) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(mcpService)
                .build();
    }

}
