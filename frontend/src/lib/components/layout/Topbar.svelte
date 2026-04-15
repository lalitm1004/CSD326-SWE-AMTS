<script lang="ts">
    import { UserStore, SupaStore } from '$lib/stores/SupaStore';
    import { rolesStore } from '$lib/stores/roles.store';
    import Badge from '$lib/components/ui/Badge.svelte';
    import { goto, invalidate } from '$app/navigation';
    import type { RoleEnumT } from '$lib/types/role.type';

    const displayRole = $derived<RoleEnumT | null>(
        ($rolesStore.find(r => r !== 'SPECTATOR') as RoleEnumT | undefined) ?? null
    );

    async function signOut() {
        await $SupaStore.auth.signOut();
        await invalidate('supabase:auth');
        await goto('/');
    }
</script>

<header class="h-12 border-b border-[var(--color-border)] bg-[var(--color-canvas)] flex items-center justify-between px-6 shrink-0">
    <div></div>
    <div class="flex items-center gap-4">
        {#if displayRole}
            <Badge variant={displayRole}>{displayRole.replace(/_/g, ' ')}</Badge>
        {/if}
        <span class="text-xs text-[var(--color-text-muted)] font-['DM_Mono']">{$UserStore?.email ?? ''}</span>
        <button
            onclick={signOut}
            class="text-xs text-[var(--color-text-muted)] hover:text-[var(--color-error)] transition-colors uppercase tracking-widest"
        >
            Sign out
        </button>
    </div>
</header>
