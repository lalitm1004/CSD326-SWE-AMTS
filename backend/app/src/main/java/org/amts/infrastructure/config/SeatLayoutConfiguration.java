package org.amts.infrastructure.config;

import org.amts.infrastructure.layout.SeatLayoutLoader;
import org.jooq.DSLContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SeatLayoutProperties.class)
public class SeatLayoutConfiguration {

    @Bean
    public SeatLayoutLoader seatLayoutLoader(DSLContext dslContext, SeatLayoutProperties properties) {
        return new SeatLayoutLoader(dslContext, properties);
    }
}
