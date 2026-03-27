<script lang="ts">
    import getCustomClaims from "$lib/utils/supabase/getCustomClaims.js";

    const { data } = $props();
    const { supabase, user } = $derived(data);

    const EMAIL = "lm742@snu.edu.in";
    const PASSWORD = "yea-bro-this-is-my-password";

    const handleSignUp = async () => {
        const { data, error } = await supabase.auth.signUp({
            email: EMAIL,
            password: PASSWORD,
        });
    };

    const handleSignIn = async () => {
        // TODO: make form
        await supabase.auth.signInWithPassword({
            email: EMAIL,
            password: PASSWORD,
        });
    };

    const handleSignOut = async () => {
        await supabase.auth.signOut();
    };
</script>

<div class={`h-dvh w-dvw flex flex-col justify-center items-center`}>
    {#if user}
        <div class={`flex flex-col`}>
            {user.email}
            <pre>{JSON.stringify(getCustomClaims(user), null, 2)}</pre>
        </div>
        <button
            onclick={() => handleSignOut()}
            class={`border-2 px-4 py-2 rounded-sm cursor-pointer`}
        >
            Sign Out
        </button>
    {:else}
        <button
            onclick={() => handleSignUp()}
            class={`border-2 px-4 py-2 rounded-sm cursor-pointer`}
        >
            Sign Up
        </button>

        <button
            onclick={() => handleSignIn()}
            class={`border-2 px-4 py-2 rounded-sm cursor-pointer`}
        >
            Sign In
        </button>
    {/if}
</div>
