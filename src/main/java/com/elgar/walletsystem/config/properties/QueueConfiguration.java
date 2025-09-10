package com.elgar.walletsystem.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix="queue")
public class QueueConfiguration {

    private QueueBinding wallet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueBinding {
        private String name;
        private String exchange;
        private String routingKey;
    }


}
