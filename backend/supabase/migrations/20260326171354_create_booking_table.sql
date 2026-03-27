CREATE TYPE "BookingTypeEnum" AS ENUM (
    'ONLINE',
    'OFFLINE',
    'COMPLEMENTARY'
);

CREATE TABLE "booking" (
    "id" UUID NOT NULL,
    "show_id" UUID,

    "type" "BookingTypeEnum" NOT NULL,

    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "booking_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "booking_show_id_fkey"
        FOREIGN KEY ("show_id")
        REFERENCES "show"("id")
        ON DELETE SET NULL
);

CREATE TABLE "complementary_booking" (
    "id" UUID NOT NULL,
    "created_by_user_id" UUID,

    CONSTRAINT "complementary_booking_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "complementary_booking_id_fkey"
        FOREIGN KEY ("id")
        REFERENCES "booking"("id")
        ON DELETE CASCADE,

    CONSTRAINT "complementary_booking_created_by_user_id_fkey"
        FOREIGN KEY ("created_by_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL
);

CREATE TABLE "offline_booking" (
    "id" UUID NOT NULL,
    "spectator_user_id" UUID,
    "sales_agent_user_id" UUID,

    CONSTRAINT "offline_booking_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "offline_booking_id_fkey"
        FOREIGN KEY ("id")
        REFERENCES "booking"("id")
        ON DELETE CASCADE,

    CONSTRAINT "offline_booking_spectator_user_id_fkey"
        FOREIGN KEY ("spectator_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL,

    CONSTRAINT "offline_booking_sales_agent_user_id_fkey"
        FOREIGN KEY ("sales_agent_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL
);

CREATE TABLE "online_booking" (
    "id" UUID NOT NULL,
    "spectator_user_id" UUID,
    "coupon_id" UUID,

    CONSTRAINT "online_booking_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "online_booking_id_fkey"
        FOREIGN KEY ("id")
        REFERENCES "booking"("id")
        ON DELETE CASCADE,

    CONSTRAINT "online_booking_spectator_user_id_fkey"
        FOREIGN KEY ("spectator_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL,

    CONSTRAINT "online_booking_coupon_id_fkey"
        FOREIGN KEY ("coupon_id")
        REFERENCES "coupon"("id")
        ON DELETE RESTRICT,

    CONSTRAINT "online_booking_coupon_id_unique"
        UNIQUE ("coupon_id")
);