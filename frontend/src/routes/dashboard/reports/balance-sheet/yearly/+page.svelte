<script lang="ts">
    import { goto } from '$app/navigation';
    import type { PageData } from './$types';
    import YearlyBalanceSheet from '$lib/components/reports/YearlyBalanceSheet.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import Input from '$lib/components/ui/Input.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';

    let { data }: { data: PageData } = $props();
    let year = $state('');

    $effect(() => {
        year = String(data.selectedYear);
    });
</script>

<svelte:head>
    <title>Yearly Balance Sheet — Dashboard</title>
</svelte:head>

<PageHeader
    title="Yearly Balance Sheet"
    breadcrumbs={[
        { label: 'Reports', href: '/dashboard/reports' },
        { label: 'Yearly' }
    ]}
/>

<div class="space-y-6">
    <form
        class="flex max-w-md items-end gap-3"
        onsubmit={(event) => {
            event.preventDefault();
            goto(`/dashboard/reports/balance-sheet/yearly?year=${year}`);
        }}
    >
        <Input label="Year" type="number" bind:value={year} class="flex-1" />
        <Button type="submit" variant="primary">Load</Button>
    </form>

    <YearlyBalanceSheet data={data.data} showNames={data.showNames} />
</div>
