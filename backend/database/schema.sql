-- CreateSchema
CREATE SCHEMA IF NOT EXISTS "public";

-- CreateEnum
CREATE TYPE "AccessTier" AS ENUM ('NONE', 'VIEW', 'CREATE', 'EDIT');

CREATE TYPE "GroupType" AS ENUM (
    'ROOT',
    'PRESIDENT',
    'AUDITORIUM_SECRETARY',
    'SHOW_MANAGER',
    'SALES_AGENT',
    'FINANCIAL_CLERK',
    'SPECTATOR'
);

CREATE TYPE "SeatType" AS ENUM ('ORDINARY', 'BALCONY');

-- ========================
-- group
-- ========================
CREATE TABLE "group" (
    "type" "GroupType" NOT NULL,
    "description" TEXT,

    CONSTRAINT "group_pkey" PRIMARY KEY ("type")
);

CREATE UNIQUE INDEX "group_type_key" ON "group"("type");

-- ========================
-- user_profile
-- ========================
CREATE TABLE "user_profile" (
    "id" UUID NOT NULL,
    "email" TEXT NOT NULL,
    "hashed_password" TEXT NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "user_profile_pkey" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "user_profile_email_key" ON "user_profile"("email");

-- Inheritance tables
CREATE TABLE "root_user_profile" () INHERITS ("user_profile");
CREATE TABLE "president_user_profile" () INHERITS ("user_profile");
CREATE TABLE "auditorium_secretary_user_profile" () INHERITS ("user_profile");
CREATE TABLE "show_manager_user_profile" () INHERITS ("user_profile");
CREATE TABLE "sales_agent_user_profile" () INHERITS ("user_profile");
CREATE TABLE "financial_clerk_user_profile" () INHERITS ("user_profile");
CREATE TABLE "spectator_user_profile" () INHERITS ("user_profile");

-- ========================
-- group_feature_access
-- ========================
CREATE TABLE "group_feature_access" (
    "group_type" "GroupType" NOT NULL,
    "feature" TEXT NOT NULL,
    "access" "AccessTier" NOT NULL,

    CONSTRAINT "group_feature_access_pkey"
    PRIMARY KEY ("group_type","feature")
);

ALTER TABLE "group_feature_access"
ADD CONSTRAINT "group_feature_access_group_type_fkey"
FOREIGN KEY ("group_type")
REFERENCES "group"("type")
ON DELETE CASCADE ON UPDATE RESTRICT;

-- ========================
-- group_user_assignment
-- ========================
CREATE TABLE "group_user_assignment" (
    "group_type" "GroupType" NOT NULL,
    "user_id" UUID NOT NULL,

    CONSTRAINT "group_user_assignment_pkey"
    PRIMARY KEY ("group_type","user_id")
);

ALTER TABLE "group_user_assignment"
ADD CONSTRAINT "group_user_assignment_group_type_fkey"
FOREIGN KEY ("group_type")
REFERENCES "group"("type")
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE "group_user_assignment"
ADD CONSTRAINT "group_user_assignment_user_id_fkey"
FOREIGN KEY ("user_id")
REFERENCES "user_profile"("id")
ON DELETE CASCADE ON UPDATE RESTRICT;

-- ========================
-- event
-- ========================
CREATE TABLE "event" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "created_by_user_id" UUID NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "thumbnail_url" TEXT,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "event_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "event"
ADD CONSTRAINT "event_created_by_user_id_fkey"
FOREIGN KEY ("created_by_user_id")
REFERENCES "user_profile"("id")
ON DELETE NO ACTION ON UPDATE RESTRICT;

-- ========================
-- show
-- ========================
CREATE TABLE "show" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "event_id" UUID NOT NULL,
    "created_by_user_id" UUID NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "thumbnail_url" TEXT,
    "starting_at" TIMESTAMP(3) NOT NULL,
    "ending_at" TIMESTAMP(3) NOT NULL,
    "ordinary_seat_price" DOUBLE PRECISION NOT NULL,
    "balcony_seat_price" DOUBLE PRECISION NOT NULL,
    "num_ordinary_seats" INTEGER NOT NULL,
    "num_balcony_seats" INTEGER NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "show_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "show"
ADD CONSTRAINT "show_created_by_user_id_fkey"
FOREIGN KEY ("created_by_user_id")
REFERENCES "user_profile"("id")
ON DELETE NO ACTION ON UPDATE RESTRICT;

ALTER TABLE "show"
ADD CONSTRAINT "show_event_id_fkey"
FOREIGN KEY ("event_id")
REFERENCES "event"("id")
ON DELETE CASCADE ON UPDATE CASCADE;

-- ========================
-- seat
-- ========================
CREATE TABLE "seat" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "number" TEXT NOT NULL,
    "seat_type" "SeatType" NOT NULL,

    CONSTRAINT "seat_pkey" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "seat_number_key" ON "seat"("number");

-- ========================
-- booking
-- ========================
CREATE TABLE "booking" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "show_id" UUID NOT NULL,
    "spectator_user_id" UUID NOT NULL,
    "coupon_id" UUID,
    "sales_agent_id" UUID,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "booking_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "booking"
ADD CONSTRAINT "booking_show_id_fkey"
FOREIGN KEY ("show_id")
REFERENCES "show"("id")
ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE "booking"
ADD CONSTRAINT "booking_spectator_user_id_fkey"
FOREIGN KEY ("spectator_user_id")
REFERENCES "user_profile"("id")
ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE "booking"
ADD CONSTRAINT "booking_sales_agent_id_fkey"
FOREIGN KEY ("sales_agent_id")
REFERENCES "user_profile"("id")
ON DELETE SET NULL ON UPDATE RESTRICT;

-- ========================
-- coupon
-- ========================
CREATE TABLE "coupon" (
    "show_id" UUID NOT NULL,
    "user_id" UUID NOT NULL,

    CONSTRAINT "coupon_pkey"
    PRIMARY KEY ("show_id","user_id")
);

ALTER TABLE "coupon"
ADD CONSTRAINT "coupon_show_id_fkey"
FOREIGN KEY ("show_id")
REFERENCES "show"("id")
ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE "coupon"
ADD CONSTRAINT "coupon_user_id_fkey"
FOREIGN KEY ("user_id")
REFERENCES "user_profile"("id")
ON DELETE CASCADE ON UPDATE RESTRICT;

-- ========================
-- ticket
-- ========================
CREATE TABLE "ticket" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "show_id" UUID NOT NULL,
    "seat_id" UUID NOT NULL,
    "booking_id" UUID NOT NULL,
    "is_refunded" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "ticket_pkey" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "ticket_show_id_seat_id_key"
ON "ticket"("show_id", "seat_id");

ALTER TABLE "ticket"
ADD CONSTRAINT "ticket_show_id_fkey"
FOREIGN KEY ("show_id")
REFERENCES "show"("id")
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "ticket"
ADD CONSTRAINT "ticket_seat_id_fkey"
FOREIGN KEY ("seat_id")
REFERENCES "seat"("id")
ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE "ticket"
ADD CONSTRAINT "ticket_booking_id_fkey"
FOREIGN KEY ("booking_id")
REFERENCES "booking"("id")
ON DELETE CASCADE ON UPDATE RESTRICT;

-- ========================
-- BalanceSheet
-- ========================
CREATE TABLE "BalanceSheet" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "financial_clerk_id" UUID NOT NULL,
    "show_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "BalanceSheet_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "BalanceSheet"
ADD CONSTRAINT "BalanceSheet_financial_clerk_id_fkey"
FOREIGN KEY ("financial_clerk_id")
REFERENCES "user_profile"("id")
ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ========================
-- Expense
-- ========================
CREATE TABLE "Expense" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "financial_clerk_id" UUID NOT NULL,
    "balance_sheet_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "Expense_pkey" PRIMARY KEY ("id")
);

ALTER TABLE "Expense"
ADD CONSTRAINT "Expense_financial_clerk_id_fkey"
FOREIGN KEY ("financial_clerk_id")
REFERENCES "user_profile"("id")
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE "Expense"
ADD CONSTRAINT "Expense_balance_sheet_id_fkey"
FOREIGN KEY ("balance_sheet_id")
REFERENCES "BalanceSheet"("id")
ON DELETE RESTRICT ON UPDATE RESTRICT;