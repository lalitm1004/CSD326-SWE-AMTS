import { env } from "$env/dynamic/public";
import { createBrowserClient, createServerClient, isBrowser } from "@supabase/ssr";
import type { LayoutLoad } from "./$types";

export const load: LayoutLoad = async ({ data, depends, fetch }) => {
    depends('supabase:auth');

    const supabaseUrl = env.PUBLIC_SUPABASE_URL ?? '';
    const supabaseKey = env.PUBLIC_SUPABASE_PUBLISHABLE_KEY ?? '';

    const supabase = isBrowser()
        ? createBrowserClient(supabaseUrl, supabaseKey, {
            global: {
                fetch,
            },
        })
        : createServerClient(supabaseUrl, supabaseKey, {
            global: {
                fetch,
            },
            cookies: {
                getAll() {
                    return data.cookies;
                },
            },
        });

    return {
        supabase,
        session: data.session,
        user: data.user
    };
}
