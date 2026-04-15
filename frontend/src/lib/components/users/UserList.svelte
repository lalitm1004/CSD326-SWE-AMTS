<script lang="ts">
    import Badge from '$lib/components/ui/Badge.svelte';
    import DataTable from '$lib/components/ui/DataTable.svelte';
    import type { UserDto } from '$lib/types/api/user.types';

    interface Props {
        users: UserDto[];
        title: string;
    }

    const columns = [
        { key: 'email', label: 'Email', sortable: true },
        { key: 'roles', label: 'Roles' },
        { key: 'actions', label: 'Actions' }
    ];

    let { users, title }: Props = $props();
</script>

<section class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
    <div class="mb-4 flex items-center justify-between gap-4">
        <div>
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-gold-dim)]">{title}</p>
            <h2 class="font-['Cormorant_Garamond'] text-2xl text-[var(--color-text)]">{users.length} users</h2>
        </div>
    </div>

    <DataTable columns={columns} rows={users as Record<string, unknown>[]}>
        {#snippet cell({ row, key })}
            {@const user = row as unknown as UserDto}
            {#if key === 'roles'}
                <div class="flex flex-wrap gap-1.5">
                    {#each user.roles as role}
                        <Badge variant={role}>{role}</Badge>
                    {/each}
                </div>
            {:else if key === 'actions'}
                <a href={`/dashboard/users/${user.id}`} class="text-sm text-[var(--color-gold)] hover:underline">
                    View details
                </a>
            {:else}
                {user.email}
            {/if}
        {/snippet}
    </DataTable>
</section>
