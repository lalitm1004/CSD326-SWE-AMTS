package org.amts.infrastructure.config;

import org.amts.adapters.usecases.ticket.ComplementaryAllocationImpl;
import org.amts.adapters.usecases.ticket.TicketPersistenceImpl;
import org.amts.adapters.usecases.ticket.TicketPurchaseImpl;
import org.amts.application.usecases.ticket.ComplementaryAllocationUseCase;
import org.amts.application.usecases.ticket.TicketPersistenceUseCase;
import org.amts.application.usecases.ticket.TicketPurchaseUseCase;
import org.amts.application.usecases.ticket.TicketUseCase;
import org.amts.application.usecases.user.UserPersistenceUseCase;
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
            TicketPersistenceUseCase ticketPersistence,
            UserPersistenceUseCase userPersistence
    ) {
        return new TicketPurchaseImpl(dslContext, ticketPersistence, userPersistence);
    }

    @Bean
    public ComplementaryAllocationUseCase complementaryAllocationUseCase(
            DSLContext dslContext,
            TicketPersistenceUseCase ticketPersistence,
            UserPersistenceUseCase userPersistence
    ) {
        return new ComplementaryAllocationImpl(dslContext, ticketPersistence, userPersistence);
    }

    @Bean
    public TicketUseCase ticketUseCases(
            TicketPersistenceUseCase ticketPersistence,
            TicketPurchaseUseCase ticketPurchase,
            ComplementaryAllocationUseCase complementaryAllocation
    ) {
        return new TicketUseCase(ticketPersistence, ticketPurchase, complementaryAllocation);
    }
}