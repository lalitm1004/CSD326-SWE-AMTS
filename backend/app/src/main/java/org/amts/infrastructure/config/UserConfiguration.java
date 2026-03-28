package org.amts.infrastructure.config;

import org.amts.adapters.persistence.UserPersistenceImpl;
import org.amts.application.usecases.user.UserPersistence;
import org.amts.application.usecases.user.UserUseCases;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {

    @Bean
    public UserPersistence userPersistence(DSLContext dslContext) {
        return new UserPersistenceImpl(dslContext);
    }

    @Bean
    public UserUseCases userUseCases(UserPersistence userPersistence) {
        return new UserUseCases(userPersistence);
    }
}
