<script lang="ts">
    import DataTable from '$lib/components/ui/DataTable.svelte';
    import StatCard from '$lib/components/ui/StatCard.svelte';
    import type { ConsolidatedYearlyDto } from '$lib/types/api/expense.types';

    interface Props {
        data: ConsolidatedYearlyDto;
        showNames?: Record<string, string>;
    }

    const columns = [
        { key: 'showName', label: 'Show', sortable: true },
        { key: 'totalExpenses', label: 'Total Expenses', sortable: true }
    ];

    let { data, showNames = {} }: Props = $props();

    function formatCurrency(value: number): string {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(value);
    }
</script>

<div class="space-y-6">
    <div class="max-w-sm">
        <StatCard label={`Total Expenses · ${data.year}`} value={formatCurrency(data.totalExpenses)} />
    </div>

    <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
        <DataTable columns={columns} rows={data.balanceSheets as Record<string, unknown>[]}>
            {#snippet cell({ row, key })}
            {@const sheet = row as typeof data.balanceSheets[number]}
            {#if key === 'showName'}
                    {sheet.showId ? (showNames[sheet.showId] ?? sheet.showId) : 'Unknown show'}
                {:else}
                    <span class="font-['DM_Mono'] text-sm text-[var(--color-gold)]">
                        {formatCurrency(sheet.totalExpenses ?? sheet.expenses.reduce((sum, expense) => sum + (expense.amount ?? 0), 0))}
                    </span>
                {/if}
            {/snippet}
        </DataTable>
    </div>
</div>
