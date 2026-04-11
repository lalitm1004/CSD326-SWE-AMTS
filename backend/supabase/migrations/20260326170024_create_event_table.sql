CREATE TABLE "event" (
    "id" UUID NOT NULL,
    "created_by_user_id" UUID,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "thumbnail_url" TEXT,

    "starting_at" TIMESTAMP(3) DEFAULT NULL,
    "ending_at"   TIMESTAMP(3) DEFAULT NULL,
    "created_at"  TIMESTAMP(3) NOT NULL DEFAULT now(),

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
    "ending_at"   TIMESTAMP(3) NOT NULL,

    "ordinary_seat_price" DOUBLE PRECISION NOT NULL,
    "balcony_seat_price"  DOUBLE PRECISION NOT NULL,
    "num_ordinary_seats"  INTEGER NOT NULL,
    "num_balcony_seats"   INTEGER NOT NULL,
    "created_at"          TIMESTAMP(3) NOT NULL DEFAULT now(),

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

CREATE OR REPLACE FUNCTION sync_event_time_bounds()
RETURNS TRIGGER
LANGUAGE plpgsql AS $$
BEGIN
    IF TG_OP = 'UPDATE' THEN
        IF OLD.event_id IS DISTINCT FROM NEW.event_id AND OLD.event_id IS NOT NULL THEN
            UPDATE "event"
            SET
                "starting_at" = (SELECT MIN("starting_at") FROM "show" WHERE "event_id" = OLD.event_id),
                "ending_at"   = (SELECT MAX("ending_at")   FROM "show" WHERE "event_id" = OLD.event_id)
            WHERE "id" = OLD.event_id;
        END IF;
    END IF;

    IF NEW.event_id IS NULL THEN
        RETURN NEW;
    END IF;

    IF TG_OP = 'INSERT' THEN
        UPDATE "event"
        SET
            "starting_at" = CASE
                WHEN "starting_at" IS NULL OR NEW.starting_at < "starting_at"
                THEN NEW.starting_at
                ELSE "starting_at"
            END,
            "ending_at" = CASE
                WHEN "ending_at" IS NULL OR NEW.ending_at > "ending_at"
                THEN NEW.ending_at
                ELSE "ending_at"
            END
        WHERE "id" = NEW.event_id;

    ELSIF TG_OP = 'UPDATE' THEN
        UPDATE "event"
        SET
            "starting_at" = (SELECT MIN("starting_at") FROM "show" WHERE "event_id" = NEW.event_id),
            "ending_at"   = (SELECT MAX("ending_at")   FROM "show" WHERE "event_id" = NEW.event_id)
        WHERE "id" = NEW.event_id;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_show_sync_event_bounds
AFTER INSERT OR UPDATE OF "starting_at", "ending_at", "event_id"
ON "show"
FOR EACH ROW
EXECUTE FUNCTION sync_event_time_bounds();