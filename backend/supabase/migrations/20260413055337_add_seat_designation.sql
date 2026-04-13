CREATE TYPE "SeatDesignationEnum" AS ENUM (
    'ORDINARY',
    'VIP',
    'COMPLIMENTARY'
);

ALTER TABLE "seat"
    ADD COLUMN "designation" "SeatDesignationEnum" NOT NULL DEFAULT 'ORDINARY';
