package org.amts.infrastructure.config;

import org.amts.adapters.usecases.finance.ExpenseTrackingImpl;
import org.amts.adapters.usecases.finance.FinancePersistenceImpl;
import org.amts.application.usecases.finance.ExpenseTrackingUseCase;
import org.amts.application.usecases.finance.rawinterfaces.FinancePersistenceUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinanceConfiguration {

    @Bean
    public FinancePersistenceUseCase financePersistenceUseCase(DSLContext dslContext) {
        return new FinancePersistenceImpl(dslContext);
    }

    @Bean
    public ExpenseTrackingUseCase expenseTrackingUseCase(
            FinancePersistenceUseCase financePersistence,
            UserPersistenceUseCase userPersistence) {
        return new ExpenseTrackingImpl(financePersistence, userPersistence);
    }
}
