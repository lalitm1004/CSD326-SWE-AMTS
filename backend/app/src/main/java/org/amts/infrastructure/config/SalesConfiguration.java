package org.amts.infrastructure.config;

import org.amts.adapters.usecases.sales.SalesPersistenceImpl;
import org.amts.adapters.usecases.sales.SalesTrackingImpl;
import org.amts.application.usecases.event.rawinterfaces.ShowPersistenceUseCase;
import org.amts.application.usecases.sales.SalesTrackingUseCase;
import org.amts.application.usecases.sales.rawinterfaces.SalesPersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SalesConfiguration {

    @Bean
    public SalesPersistenceUseCase salesPersistence(DSLContext dslContext) {
        return new SalesPersistenceImpl(dslContext);
    }

    @Bean
    public SalesTrackingUseCase salesTrackingUseCase(
            SalesPersistenceUseCase salesPersistence,
            UserPersistenceUseCase userPersistence,
            ShowPersistenceUseCase showPersistence) {
        return new SalesTrackingImpl(salesPersistence, userPersistence, showPersistence);
    }
}
