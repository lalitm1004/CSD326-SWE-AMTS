CREATE TYPE "SeatTypeEnum" AS ENUM (
    'ORDINARY',
    'BALCONY'
);

CREATE TABLE "seat" (
    "id" UUID NOT NULL,
    
    "number" TEXT NOT NULL,
    "type" "SeatTypeEnum" NOT NULL,

    CONSTRAINT "seat_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "seat_number_unique"
        UNIQUE ("number")
);