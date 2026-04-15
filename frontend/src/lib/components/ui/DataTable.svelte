<script lang="ts" generics="T extends Record<string, unknown>">
    import type { Snippet } from 'svelte';

    interface Column {
        key: string;
        label: string;
        sortable?: boolean;
    }

    interface Props {
        columns: Column[];
        rows: T[];
        loading?: boolean;
        cell?: Snippet<[{ row: T; key: string }]>;
        emptyTitle?: string;
        emptyBody?: string;
    }

    let { columns, rows, loading = false, cell, emptyTitle = 'No records', emptyBody }: Props = $props();

    let sortKey = $state<string | null>(null);
    let sortDir = $state<'asc' | 'desc'>('asc');

    function toggleSort(key: string) {
        if (sortKey === key) {
            sortDir = sortDir === 'asc' ? 'desc' : 'asc';
        } else {
            sortKey = key;
            sortDir = 'asc';
        }
    }

    const sorted = $derived(() => {
        if (!sortKey) return rows;
        return [...rows].sort((a, b) => {
            const av = a[sortKey!];
            const bv = b[sortKey!];
            const cmp = String(av ?? '').localeCompare(String(bv ?? ''), undefined, { numeric: true });
            return sortDir === 'asc' ? cmp : -cmp;
        });
    });
</script>

<div class="w-full overflow-x-auto">
    <table class="w-full text-sm border-collapse">
        <thead>
            <tr class="border-b border-[var(--color-border)]">
                {#each columns as col}
                    <th
                        class="text-left px-4 py-3 text-[10px] uppercase tracking-widest font-medium text-[var(--color-text-muted)] whitespace-nowrap
                            {col.sortable ? 'cursor-pointer hover:text-[var(--color-gold)] select-none' : ''}"
                        onclick={() => col.sortable && toggleSort(col.key)}
                    >
                        <span class="flex items-center gap-1.5">
                            {col.label}
                            {#if col.sortable}
                                <span class="opacity-40 {sortKey === col.key ? 'opacity-100 text-[var(--color-gold)]' : ''}">
                                    {sortKey === col.key ? (sortDir === 'asc' ? '↑' : '↓') : '↕'}
                                </span>
                            {/if}
                        </span>
                    </th>
                {/each}
            </tr>
        </thead>
        <tbody>
            {#if loading}
                {#each { length: 5 } as _}
                    <tr class="border-b border-[var(--color-border)]/50">
                        {#each columns as _col}
                            <td class="px-4 py-3">
                                <div class="skeleton-shimmer h-4 w-full max-w-32"></div>
                            </td>
                        {/each}
                    </tr>
                {/each}
            {:else if sorted().length === 0}
                <tr>
                    <td colspan={columns.length} class="px-4 py-12 text-center text-[var(--color-text-muted)] text-sm">
                        <div class="flex flex-col items-center gap-2">
                            <span class="text-[var(--color-gold-dim)] opacity-40 text-3xl">◆</span>
                            <span>{emptyTitle}</span>
                            {#if emptyBody}<span class="text-xs">{emptyBody}</span>{/if}
                        </div>
                    </td>
                </tr>
            {:else}
                {#each sorted() as row}
                    <tr class="border-b border-[var(--color-border)]/50 hover:bg-[var(--color-raised)] transition-colors">
                        {#each columns as col}
                            <td class="px-4 py-3 text-[var(--color-text)]">
                                {#if cell}
                                    {@render cell({ row, key: col.key })}
                                {:else}
                                    {row[col.key] ?? '—'}
                                {/if}
                            </td>
                        {/each}
                    </tr>
                {/each}
            {/if}
        </tbody>
    </table>
</div>
