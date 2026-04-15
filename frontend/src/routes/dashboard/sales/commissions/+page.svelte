<script lang="ts">
    import type { PageData } from './$types';
    import CommissionTable from '$lib/components/sales/CommissionTable.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import StatCard from '$lib/components/ui/StatCard.svelte';

    let { data }: { data: PageData } = $props();

    function formatCurrency(value: number): string {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(value);
    }
</script>

<svelte:head>
    <title>Commissions — Dashboard</title>
</svelte:head>

<PageHeader title="Your Commissions" subtitle="Track total offline commission earned as a sales agent." />

<div class="space-y-6">
    <div class="max-w-sm">
        <StatCard label="Total Commission" value={formatCurrency(data.totalCommission)} />
    </div>
    <CommissionTable commissions={data.commissions} />
</div>
