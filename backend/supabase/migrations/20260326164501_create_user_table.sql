CREATE TYPE "RoleEnum" AS ENUM (
    'ROOT',
    'PRESIDENT',
    'AUDITORIUM_SECRETARY',
    'SHOW_MANAGER',
    'SALES_AGENT',
    'FINANCIAL_CLERK',
    'SPECTATOR'
);


CREATE TABLE "user_profile" (
    "id" UUID NOT NULL,
    
    "email" TEXT NOT NULL,
    
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT now(),

    CONSTRAINT "user_profile_pkey" PRIMARY KEY ("id"),
    
    CONSTRAINT "user_profile_email_key" UNIQUE ("email")
);


CREATE TABLE "user_role_assignment" (
    "user_id" UUID NOT NULL,
    "role" "RoleEnum" NOT NULL,

    CONSTRAINT "user_role_assignment_pkey" PRIMARY KEY ("user_id", "role"),

    CONSTRAINT "user_role_assignment_user_id_fkey"
        FOREIGN KEY ("user_id")
        REFERENCES "user_profile"("id")
        ON DELETE CASCADE
);



-- Create User
CREATE OR REPLACE FUNCTION fn_on_auth_new_user()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    SECURITY DEFINER
    SET search_path = ''
    AS $$
    BEGIN
        INSERT INTO public.user_profile (id, email)
        VALUES (NEW.id, '');

        UPDATE public.user_profile SET
            email = COALESCE(NEW.email, '')
        WHERE id = NEW.id;

        PERFORM public.fn_update_user_custom_claims(NEW.id);

        RETURN NEW;
    END;
    $$;

CREATE OR REPLACE TRIGGER tr_on_auth_new_user
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE PROCEDURE fn_on_auth_new_user();

-- Update User
CREATE OR REPLACE FUNCTION fn_build_user_roles(u_id UUID)
    RETURNS JSONB
    LANGUAGE plpgsql
    SECURITY DEFINER
    SET search_path = ''
    AS $$
    DECLARE
        result JSONB;
    BEGIN
        SELECT COALESCE(
            jsonb_agg(role),
            '[]'::jsonb
        )
        INTO result
        FROM public.user_role_assignment
        WHERE user_id = u_id;

        RETURN result;
    END;
    $$;

CREATE OR REPLACE FUNCTION fn_update_user_custom_claims(u_id UUID)
    RETURNS VOID
    LANGUAGE plpgsql
    SECURITY DEFINER
    SET search_path = ''
    AS $$
    DECLARE
        user_roles JSONB;
    BEGIN
        SELECT public.fn_build_user_roles(u_id) INTO user_roles;

        UPDATE auth.users
        SET raw_app_meta_data = jsonb_set(
            COALESCE(raw_app_meta_data, '{}'::jsonb),
            '{custom_claims}',
            jsonb_build_object(
                'roles', user_roles
            ),
            true
        )
        WHERE id = u_id;
    END;
    $$;

CREATE OR REPLACE FUNCTION fn_on_user_role_assignment_change()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    SECURITY DEFINER
    SET search_path = ''
    AS $$
    BEGIN
        IF TG_OP = 'DELETE' THEN
            PERFORM public.fn_update_user_custom_claims(OLD.user_id);
            RETURN OLD;
        ELSE
            PERFORM public.fn_update_user_custom_claims(NEW.user_id);
            RETURN NEW;
        END IF;
    END;
    $$;


CREATE OR REPLACE TRIGGER tr_on_user_role_assignment_change
    AFTER INSERT OR DELETE ON public.user_role_assignment
    FOR EACH ROW EXECUTE PROCEDURE fn_on_user_role_assignment_change();


CREATE OR REPLACE FUNCTION fn_on_user_update()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    SECURITY DEFINER
    SET search_path = ''
    AS $$
    BEGIN
        PERFORM public.fn_update_user_custom_claims(NEW.id);
        RETURN NEW;
    END;
    $$;

CREATE OR REPLACE TRIGGER tr_on_user_update
    AFTER UPDATE ON public.user_profile
    FOR EACH ROW EXECUTE PROCEDURE fn_on_user_update();

-- Delete User
CREATE OR REPLACE FUNCTION fn_on_auth_delete_user()
    RETURNS trigger
    LANGUAGE plpgsql
    SECURITY DEFINER
    SET search_path = ''
    AS $$
    BEGIN
        DELETE FROM public.user_profile WHERE id = old.id;
        RETURN old;
    END;
    $$;

CREATE OR REPLACE TRIGGER tr_on_auth_delete_user
    AFTER DELETE ON auth.users
    FOR EACH ROW EXECUTE PROCEDURE fn_on_auth_delete_user();