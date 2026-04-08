package org.amts.infrastructure.config;

import org.amts.adapters.usecases.event.EventManagementImpl;
import org.amts.adapters.usecases.event.EventPersistenceImpl;
import org.amts.application.usecases.event.EventManagementUseCase;
import org.amts.application.usecases.event.rawinterfaces.EventPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {
    @Bean
    public EventPersistenceUseCase assignEventPersistenceUseCase(
        DSLContext dslContext
    ) {
        return new EventPersistenceImpl(dslContext);
    }

    @Bean
    public EventManagementUseCase eventManagementUseCase(
        EventPersistenceUseCase eventPersistenceUseCase,
        UserPersistenceUseCase userPersistenceUseCase
    ) {
        return new EventManagementImpl(eventPersistenceUseCase, userPersistenceUseCase);
    }
}
