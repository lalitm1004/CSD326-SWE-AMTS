<script lang="ts">
    import RevenueChart from '$lib/components/sales/RevenueChart.svelte';
    import DataTable from '$lib/components/ui/DataTable.svelte';
    import StatCard from '$lib/components/ui/StatCard.svelte';
    import type { RevenueDto } from '$lib/types/api/sales.types';

    interface Props {
        revenues: RevenueDto[];
    }

    const columns = [
        { key: 'showName', label: 'Show', sortable: true },
        { key: 'totalRevenue', label: 'Revenue', sortable: true }
    ];

    let { revenues }: Props = $props();

    const totalRevenue = $derived(revenues.reduce((sum, item) => sum + item.totalRevenue, 0));

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
        <StatCard label="Total Revenue" value={formatCurrency(totalRevenue)} />
    </div>

    <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
        <RevenueChart items={revenues.map((item) => ({ label: item.showName ?? item.showId, value: item.totalRevenue }))} />
    </div>

    <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
        <DataTable columns={columns} rows={revenues as Record<string, unknown>[]}>
            {#snippet cell({ row, key })}
                {@const revenue = row as unknown as RevenueDto}
                {#if key === 'showName'}
                    {revenue.showName ?? revenue.showId}
                {:else}
                    <span class="font-['DM_Mono'] text-sm text-[var(--color-gold)]">{formatCurrency(revenue.totalRevenue)}</span>
                {/if}
            {/snippet}
        </DataTable>
    </div>
</div>
