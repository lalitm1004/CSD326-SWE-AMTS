<script lang="ts">
    import type { PageData } from './$types';
    import Badge from '$lib/components/ui/Badge.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import UserRoleEditor from '$lib/components/users/UserRoleEditor.svelte';

    let { data }: { data: PageData } = $props();
</script>

<svelte:head>
    <title>{data.user.email} — User Roles</title>
</svelte:head>

<PageHeader
    title={data.user.email}
    subtitle="Review and adjust role assignments."
    breadcrumbs={[
        { label: 'Users', href: '/dashboard/users' },
        { label: data.user.email }
    ]}
/>

<div class="space-y-6">
    <section class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
        <p class="mb-1 text-[10px] uppercase tracking-widest text-[var(--color-text-muted)]">User ID</p>
        <p class="font-['DM_Mono'] text-sm text-[var(--color-text-muted)]">{data.user.id}</p>

        <div class="mt-5">
            <p class="mb-2 text-[10px] uppercase tracking-widest text-[var(--color-text-muted)]">Assigned Roles</p>
            <div class="flex flex-wrap gap-2">
                {#each data.user.roles as role}
                    <Badge variant={role}>{role}</Badge>
                {/each}
            </div>
        </div>
    </section>

    <UserRoleEditor user={data.user} actorUserId={data.actorUserId} actorRoles={data.actorRoles} />
</div>
