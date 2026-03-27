CREATE TABLE "balance_sheet" (
    "id" UUID NOT NULL,
    "financial_clerk_user_id" UUID,
    "show_id" UUID,

    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "balance_sheet_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "balance_sheet_financial_clerk_user_id_fkey"
        FOREIGN KEY ("financial_clerk_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL,
    
    CONSTRAINT "balance_sheet_show_id_fkey"
        FOREIGN KEY ("show_id")
        REFERENCES "show"("id")
        ON DELETE SET NULL,

    CONSTRAINT "balance_sheet_show_id_unique"
        UNIQUE ("show_id")
);

CREATE TABLE "expense" (
    "id" UUID NOT NULL,
    "financial_clerk_user_id" UUID,
    "balance_sheet_id" UUID NOT NULL,

    "name" TEXT NOT NULL,
    "description" TEXT,
    "amount" DOUBLE PRECISION NOT NULL,

    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "expense_pkey" PRIMARY KEY ("id"),

    CONSTRAINT "expense_financial_clerk_fkey"
        FOREIGN KEY ("financial_clerk_user_id")
        REFERENCES "user_profile"("id")
        ON DELETE SET NULL,
    
    CONSTRAINT "expense_balance_sheet_id_fkey"
        FOREIGN KEY ("balance_sheet_id")
        REFERENCES "balance_sheet"("id")
        ON DELETE RESTRICT
);