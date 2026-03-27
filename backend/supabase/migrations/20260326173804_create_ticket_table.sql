CREATE TABLE "ticket" (
    "id" UUID NOT NULL,
    "booking_id" UUID,
    "show_id" UUID,
    "seat_id" UUID,

    "code" TEXT NOT NULL,
    "is_refunded" BOOLEAN DEFAULT false,

    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "ticket_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "ticket_booking_id_fkey"
        FOREIGN KEY ("booking_id")
        REFERENCES "booking"("id")
        ON DELETE CASCADE,

    CONSTRAINT "ticket_show_id_fkey"
        FOREIGN KEY ("show_id")
        REFERENCES "show"("id")
        ON DELETE CASCADE,

    CONSTRAINT "ticket_seat_id_fkey"
        FOREIGN KEY ("seat_id")
        REFERENCES "seat"("id")
        ON DELETE SET NULL,

    CONSTRAINT "ticket_show_id_seat_id_unique"
        UNIQUE ("show_id", "seat_id"),

    CONSTRAINT "ticket_code_unique"
        UNIQUE ("code")
);