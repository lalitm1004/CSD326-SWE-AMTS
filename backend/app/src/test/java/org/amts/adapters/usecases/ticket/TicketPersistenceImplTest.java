package org.amts.adapters.usecases.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.amts.application.exceptions.PersistenceException;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

class TicketPersistenceImplTest {

    @Test
    void saveCouponRejectsDuplicateCouponForSameShow() {
        DSLContext dsl = mock(DSLContext.class, Answers.RETURNS_DEEP_STUBS);
        when(dsl.fetchExists(any(Select.class))).thenReturn(true);

        TicketPersistenceImpl persistence = new TicketPersistenceImpl(dsl);

        PersistenceException ex = assertThrows(
                PersistenceException.class,
                () -> persistence.saveCoupon(UUID.randomUUID(), UUID.randomUUID())
        );

        assertEquals("You can only purchase one coupon per show", ex.getMessage());
    }
}
