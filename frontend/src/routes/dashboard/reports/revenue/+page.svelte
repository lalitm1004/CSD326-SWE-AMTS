<script lang="ts">
    import { goto } from '$app/navigation';
    import type { PageData } from './$types';
    import RevenueReport from '$lib/components/reports/RevenueReport.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Select from '$lib/components/ui/Select.svelte';

    let { data }: { data: PageData } = $props();
</script>

<svelte:head>
    <title>Revenue Report — Dashboard</title>
</svelte:head>

<PageHeader title="Revenue Report" breadcrumbs={[{ label: 'Reports', href: '/dashboard/reports' }, { label: 'Revenue' }]} />

<div class="space-y-6">
    <div class="max-w-md">
        <Select
            label="Event"
            options={data.events.map((event) => ({ value: event.id, label: event.name }))}
            value={data.selectedEventId}
            placeholder="Select event…"
            onchange={(event) => {
                const eventId = (event.currentTarget as HTMLSelectElement).value;
                goto(`/dashboard/reports/revenue${eventId ? `?eventId=${eventId}` : ''}`);
            }}
        />
    </div>

    {#if data.selectedEventId}
        <RevenueReport revenues={data.revenues} />
    {:else}
        <EmptyState title="Choose an event" body="Select an event to load its revenue breakdown." />
    {/if}
</div>
