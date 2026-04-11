package org.amts.infrastructure.config;

import org.amts.adapters.usecases.event.ShowManagementImpl;
import org.amts.adapters.usecases.event.ShowPersistenceImpl;
import org.amts.application.usecases.event.ShowManagementUseCase;
import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShowConfiguration {
    @Bean
    public ShowPersistenceUseCase assignShowPersistenceUseCase(
        DSLContext dslContext
    ) {
        return new ShowPersistenceImpl(dslContext);
    }

    @Bean
    public ShowManagementUseCase showManagementUseCase(
        ShowPersistenceUseCase showPersistenceUseCase,
        UserPersistenceUseCase userPersistenceUseCase
    ) {
        return new ShowManagementImpl(showPersistenceUseCase, userPersistenceUseCase);
    }
}