<script lang="ts">
    import "$lib/styles/globals.css";

    import favicon from "$lib/assets/favicon.svg";
    import { onMount } from "svelte";
    import { invalidate } from "$app/navigation";
    import {
        SessionStore,
        SupaStore,
        UserStore,
    } from "$lib/stores/SupaStore.js";
    import ToastContainer from "$lib/components/ui/ToastContainer.svelte";

    let { data, children } = $props();
    let { supabase, session, user } = $derived(data);

    $effect(() => {
        SupaStore.set(supabase);
        UserStore.set(user);
        SessionStore.set(session);
    });

    onMount(() => {
        const {
            data: { subscription },
        } = supabase.auth.onAuthStateChange((event, newSession) => {
            SessionStore.set(newSession);
            UserStore.set(newSession?.user ?? null);

            if (event === "SIGNED_IN" || event === "TOKEN_REFRESHED" || event === "USER_UPDATED" || event === "SIGNED_OUT") {
                invalidate("supabase:auth");
            }
        });

        return () => subscription.unsubscribe();
    });
</script>

<svelte:head>
    <link rel="icon" href={favicon} />
</svelte:head>

<div class="font-['DM_Sans'] bg-[var(--color-canvas)] text-[var(--color-text)] min-h-dvh">
    {@render children()}
</div>

<ToastContainer />
