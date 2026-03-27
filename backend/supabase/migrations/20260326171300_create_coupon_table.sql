CREATE TABLE "coupon" (
    "id" UUID NOT NULL,
    "show_id" UUID,
    "spectator_user_id" UUID,

    "code" TEXT NOT NULL,

    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "coupon_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "coupon_show_id_fkey"
        FOREIGN KEY ("show_id")
        REFERENCES "show"("id")
        ON DELETE SET NULL,

    CONSTRAINT "coupon_spectator_user_id_fkey"
        FOREIGN KEY ("spectator_user_id")
        REFERENCES "user_profile"("id") ON DELETE SET NULL,

    CONSTRAINT "coupon_show_id_user_id_unique"
        UNIQUE ("show_id", "spectator_user_id")
);