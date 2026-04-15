<script lang="ts">
    import DataTable from '$lib/components/ui/DataTable.svelte';
    import type { CommissionDto } from '$lib/types/api/sales.types';

    interface Props {
        commissions: CommissionDto[];
    }

    const columns = [
        { key: 'label', label: 'Scope' },
        { key: 'totalSales', label: 'Total Sales', sortable: true },
        { key: 'commission', label: 'Commission Earned', sortable: true }
    ];

    let { commissions }: Props = $props();

    const totalSales = $derived(commissions.reduce((sum, item) => sum + item.totalSales, 0));
    const totalCommission = $derived(commissions.reduce((sum, item) => sum + item.commission, 0));

    function formatCurrency(value: number): string {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(value);
    }

    function getLabel(item: CommissionDto): string {
        return item.agentEmail ?? item.eventId ?? item.showId ?? item.agentUserId;
    }
</script>

<div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
    <DataTable columns={columns} rows={commissions as Record<string, unknown>[]}>
        {#snippet cell({ row, key })}
            {@const item = row as unknown as CommissionDto}
            {#if key === 'label'}
                <span class="text-sm text-[var(--color-text)]">{getLabel(item)}</span>
            {:else if key === 'totalSales'}
                <span class="font-['DM_Mono'] text-sm text-[var(--color-text)]">{formatCurrency(item.totalSales)}</span>
            {:else if key === 'commission'}
                <span class="font-['DM_Mono'] text-sm text-[var(--color-gold)]">{formatCurrency(item.commission)}</span>
            {/if}
        {/snippet}
    </DataTable>

    <div class="mt-4 flex items-center justify-between border-t border-[var(--color-border)] pt-4">
        <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)]">Totals</p>
        <div class="flex items-center gap-6">
            <p class="font-['DM_Mono'] text-sm text-[var(--color-text)]">{formatCurrency(totalSales)}</p>
            <p class="font-['DM_Mono'] text-sm text-[var(--color-gold)]">{formatCurrency(totalCommission)}</p>
        </div>
    </div>
</div>
