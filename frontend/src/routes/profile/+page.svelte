<script lang="ts">
    import { UserStore, SupaStore } from '$lib/stores/SupaStore';
    import { rolesStore } from '$lib/stores/roles.store';
    import { goto, invalidate } from '$app/navigation';
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Badge from '$lib/components/ui/Badge.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import Divider from '$lib/components/ui/Divider.svelte';
    import type { RoleEnumT } from '$lib/types/role.type';

    async function signOut() {
        await $SupaStore.auth.signOut();
        await invalidate('supabase:auth');
        await goto('/');
    }
</script>

<svelte:head>
    <title>Profile — AMTS</title>
</svelte:head>

<PublicNav />

<div class="max-w-2xl mx-auto px-6 py-12">
    <PageHeader title="Your Profile" />
    <Divider />

    <div class="mt-8 bg-[var(--color-surface)] border border-[var(--color-border)] p-6 corner-ornament">
        <div class="flex flex-col gap-5">
            <div>
                <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)] mb-1">Email</p>
                <p class="font-['DM_Mono'] text-sm text-[var(--color-text)]">{$UserStore?.email ?? '—'}</p>
            </div>
            <div>
                <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)] mb-2">Roles</p>
                <div class="flex flex-wrap gap-2">
                    {#each $rolesStore as role}
                        <Badge variant={role as RoleEnumT}>{role.replace(/_/g, ' ')}</Badge>
                    {/each}
                    {#if $rolesStore.length === 0}
                        <span class="text-sm text-[var(--color-text-muted)]">No roles assigned</span>
                    {/if}
                </div>
            </div>
        </div>
    </div>

    <div class="mt-6">
        <Button variant="ghost" onclick={signOut}>Sign out</Button>
    </div>
</div>
