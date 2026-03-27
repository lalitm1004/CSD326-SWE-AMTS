CREATE TABLE "event" (
    "id" UUID NOT NULL,
    "created_by_user_id" UUID,

    "name" TEXT NOT NULL,
    "description" TEXT,
    "thumbnail_url" TEXT,
    
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "event_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "event_created_by_user_id_fkey"
        FOREIGN KEY ("created_by_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL
);


CREATE TABLE "show" (
    "id" UUID NOT NULL,
    "event_id" UUID,
    "created_by_user_id" UUID,
    
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

    CONSTRAINT "show_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "show_event_id_fkey"
        FOREIGN KEY ("event_id")
        REFERENCES "event"("id")
        ON DELETE CASCADE,
    
    CONSTRAINT "show_created_by_user_id_fkey"
        FOREIGN KEY ("created_by_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL
);
