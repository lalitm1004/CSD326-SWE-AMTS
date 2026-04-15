<script lang="ts">
    import { SupaStore } from '$lib/stores/SupaStore';
    import { goto, invalidate } from '$app/navigation';
    import { toastStore } from '$lib/stores/toast.store';
    import Button from '$lib/components/ui/Button.svelte';
    import Input from '$lib/components/ui/Input.svelte';
    import getCustomClaims from '$lib/utils/supabase/getCustomClaims';

    const ADMIN_ROLES = ['ROOT', 'PRESIDENT', 'AUDITORIUM_SECRETARY', 'SHOW_MANAGER', 'SALES_AGENT', 'FINANCIAL_CLERK'];

    let mode = $state<'signin' | 'signup'>('signin');
    let email = $state('');
    let password = $state('');
    let loading = $state(false);
    let error = $state('');

    async function handleSubmit(e: SubmitEvent) {
        e.preventDefault();
        loading = true;
        error = '';

        try {
            if (mode === 'signin') {
                const { data, error: err } = await $SupaStore.auth.signInWithPassword({ email, password });
                if (err) throw err;

                const roles = getCustomClaims(data.user)?.roles ?? [];
                const destination = roles.some((role) => ADMIN_ROLES.includes(role))
                    ? '/dashboard'
                    : '/';

                await invalidate('supabase:auth');
                toastStore.add('success', 'Signed in successfully');
                await goto(destination);
            } else {
                const { error: err } = await $SupaStore.auth.signUp({ email, password });
                if (err) throw err;
                toastStore.add('success', 'Account created. You can now sign in.');
                mode = 'signin';
            }
        } catch (err: unknown) {
            error = (err as { message?: string }).message ?? 'Something went wrong';
        } finally {
            loading = false;
        }
    }
</script>

<div class="w-full max-w-sm">
    <!-- Mode toggle -->
    <div class="flex border-b border-[var(--color-border)] mb-8">
        <button
            class="flex-1 py-3 text-sm font-medium transition-colors border-b-2 -mb-px
                {mode === 'signin' ? 'text-[var(--color-gold)] border-[var(--color-gold)]' : 'text-[var(--color-text-muted)] border-transparent hover:text-[var(--color-text)]'}"
            onclick={() => { mode = 'signin'; error = ''; }}
        >
            Sign In
        </button>
        <button
            class="flex-1 py-3 text-sm font-medium transition-colors border-b-2 -mb-px
                {mode === 'signup' ? 'text-[var(--color-gold)] border-[var(--color-gold)]' : 'text-[var(--color-text-muted)] border-transparent hover:text-[var(--color-text)]'}"
            onclick={() => { mode = 'signup'; error = ''; }}
        >
            Create Account
        </button>
    </div>

    <form onsubmit={handleSubmit} class="flex flex-col gap-5">
        <Input
            label="Email"
            type="email"
            placeholder="you@example.com"
            required
            bind:value={email}
        />
        <Input
            label="Password"
            type="password"
            placeholder="••••••••"
            required
            bind:value={password}
            error={error || undefined}
        />
        <Button type="submit" {loading} class="w-full mt-1">
            {mode === 'signin' ? 'Sign In' : 'Create Account'}
        </Button>
    </form>
</div>
