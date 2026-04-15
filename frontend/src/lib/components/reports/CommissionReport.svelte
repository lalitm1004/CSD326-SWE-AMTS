<script lang="ts">
    import CommissionTable from '$lib/components/sales/CommissionTable.svelte';
    import StatCard from '$lib/components/ui/StatCard.svelte';
    import type { CommissionDto } from '$lib/types/api/sales.types';

    interface Props {
        commissions: CommissionDto[];
    }

    let { commissions }: Props = $props();

    const totalCommission = $derived(commissions.reduce((sum, item) => sum + item.commission, 0));

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
        <StatCard label="Total Commission" value={formatCurrency(totalCommission)} />
    </div>
    <CommissionTable {commissions} />
</div>
