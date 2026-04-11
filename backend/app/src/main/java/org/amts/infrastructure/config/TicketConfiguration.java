package org.amts.infrastructure.config;

import org.amts.adapters.usecases.ticket.TicketPersistenceImpl;
import org.amts.adapters.usecases.ticket.TicketPurchaseUseCaseImpl;
import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.ticket.TicketPurchaseUseCase;
import org.amts.application.usecases.ticket.TicketUseCases;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketConfiguration {

    @Bean
    public TicketPersistenceUseCase ticketPersistenceUseCase(DSLContext dslContext) {
        return new TicketPersistenceImpl(dslContext);
    }

    @Bean
    public TicketPurchaseUseCase ticketPurchaseUseCase(
            DSLContext dslContext,
            TicketPersistenceUseCase ticketPersistence) {
        return new TicketPurchaseUseCaseImpl(dslContext, ticketPersistence);
    }

    @Bean
    public TicketUseCases ticketUseCases(
            TicketPersistenceUseCase ticketPersistence,
            TicketPurchaseUseCase ticketPurchase) {
        return new TicketUseCases(ticketPersistence, ticketPurchase);
    }
}
