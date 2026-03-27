CREATE TYPE "RefundType" AS ENUM (
    'BEFORE_THREE_DAYS',
    'BEFORE_ONE_DAY',
    'SAME_DAY'
);

CREATE TABLE "ticket_refund" (
    "ticket_id" UUID NOT NULL,

    "type" "RefundType" NOT NULL,

    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "ticket_refund_pkey" PRIMARY KEY ("ticket_id"),

    CONSTRAINT "ticket_refund_ticket_id_fkey"
        FOREIGN KEY ("ticket_id")
        REFERENCES "ticket"("id")
        ON DELETE CASCADE
);