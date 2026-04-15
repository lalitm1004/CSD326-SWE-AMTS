<script lang="ts">
    import type { PageData } from './$types';
    import CommissionReport from '$lib/components/reports/CommissionReport.svelte';
    import RevenueReport from '$lib/components/reports/RevenueReport.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';

    let { data }: { data: PageData } = $props();
</script>

<svelte:head>
    <title>{data.show?.name ?? 'Show'} Sales — Dashboard</title>
</svelte:head>

<PageHeader
    title={`${data.show?.name ?? 'Show'} Sales`}
    breadcrumbs={[
        { label: 'Events', href: '/dashboard/events' },
        { label: 'Show', href: `/dashboard/events/${data.show?.eventId}/shows/${data.show?.id}` },
        { label: 'Sales' }
    ]}
/>

<div class="space-y-8">
    <RevenueReport revenues={data.revenues} />
    <CommissionReport commissions={data.commissions} />
</div>
