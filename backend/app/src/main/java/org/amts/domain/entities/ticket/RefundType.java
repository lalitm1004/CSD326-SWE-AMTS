package org.amts.domain.entities.ticket;

public enum RefundType {
    /**
     * Cancelled more than 3 days before the show
     * Rs. 5 deduction per ticket
     */
    BEFORE_THREE_DAYS,

    /**
     * Cancelled at least 1 day before the show
     * Rs. 10 (ordinary)
     * Rs. 15 (balcony) per ticket
     */
    BEFORE_ONE_DAY,

    /**
     * Cancelled on the day of the show
     * 50% deduction
     */
    SAME_DAY
}
