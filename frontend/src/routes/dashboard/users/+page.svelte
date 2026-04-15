<script lang="ts">
    import { goto } from '$app/navigation';
    import type { PageData } from './$types';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import UserSearchForm from '$lib/components/users/UserSearchForm.svelte';
    import UserList from '$lib/components/users/UserList.svelte';
    import Select from '$lib/components/ui/Select.svelte';
    import { RoleEnum, type RoleEnumT } from '$lib/types/role.type';

    let { data }: { data: PageData } = $props();
    let selectedRole = $state('');

    const roleOptions = [
        { value: '', label: 'All roles' },
        ...RoleEnum.options.map((role) => ({ value: role, label: role.replaceAll('_', ' ') }))
    ];

    const filteredUsers = $derived(
        selectedRole
            ? data.users.filter((user) => user.roles.includes(selectedRole as RoleEnumT))
            : data.users
    );
</script>

<svelte:head>
    <title>Users — Dashboard</title>
</svelte:head>

<PageHeader title="Users" subtitle="Search for users and manage operational roles." />

<div class="space-y-6">
    <UserSearchForm onFound={(user) => goto(`/dashboard/users/${user.id}`)} />
    <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
        <div class="grid gap-4 lg:grid-cols-[minmax(0,18rem)_1fr] lg:items-end">
            <Select
                label="Filter By Role"
                options={roleOptions}
                bind:value={selectedRole}
            />
            <p class="text-sm text-[var(--color-text-muted)] lg:text-right">
                Showing <span class="text-[var(--color-text)]">{filteredUsers.length}</span> of <span class="text-[var(--color-text)]">{data.users.length}</span> users
            </p>
        </div>
    </div>
    <UserList title="All Users" users={filteredUsers} />
</div>
