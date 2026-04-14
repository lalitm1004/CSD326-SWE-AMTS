-- FR-TS5: allow seat_id to become NULL when a ticket is refunded (seat returned to available pool)
ALTER TABLE "ticket" ALTER COLUMN "seat_id" DROP NOT NULL;

-- FR-TS6: audit table for commission invalidation events when an agent-sold ticket is refunded
CREATE TABLE "commission_invalidation" (
    "id"                   UUID NOT NULL,
    "ticket_id"            UUID NOT NULL,
    "sales_agent_user_id"  UUID,
    "created_at"           TIMESTAMP(3) NOT NULL DEFAULT now(),
    CONSTRAINT "commission_invalidation_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "commission_invalidation_ticket_id_fkey"
        FOREIGN KEY ("ticket_id") REFERENCES "ticket"("id") ON DELETE CASCADE,
    CONSTRAINT "commission_invalidation_agent_fkey"
        FOREIGN KEY ("sales_agent_user_id") REFERENCES "user_profile"("id") ON DELETE SET NULL
);
