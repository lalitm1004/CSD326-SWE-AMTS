package org.amts.infrastructure.config;

import org.amts.adapters.usecases.user.UserRoleManagementImpl;
import org.amts.adapters.usecases.user.UserPersistenceImpl;
import org.amts.application.usecases.user.UserRoleManagementUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.application.usecases.user.UserUseCases;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {
    @Bean
    public UserPersistenceUseCase userPersistence(DSLContext dslContext) {
        return new UserPersistenceImpl(dslContext);
    }

    @Bean
    public UserRoleManagementUseCase assignUserRolesUseCase(UserPersistenceUseCase userPersistence) {
        return new UserRoleManagementImpl(userPersistence);
    }

    @Bean
    public UserUseCases userUseCases(UserPersistenceUseCase userPersistence,
            UserRoleManagementUseCase assignUserRoles) {
        return new UserUseCases(userPersistence, assignUserRoles);
    }
}
