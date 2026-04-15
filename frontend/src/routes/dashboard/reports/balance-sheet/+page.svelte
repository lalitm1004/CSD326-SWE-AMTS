<script lang="ts">
    import { goto } from '$app/navigation';
    import type { PageData } from './$types';
    import BalanceSheet from '$lib/components/expenses/BalanceSheet.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Select from '$lib/components/ui/Select.svelte';

    let { data }: { data: PageData } = $props();

    function updateSelection(eventId: string, showId = '') {
        const params = new URLSearchParams();
        if (eventId) params.set('eventId', eventId);
        if (showId) params.set('showId', showId);
        goto(`/dashboard/reports/balance-sheet${params.size > 0 ? `?${params.toString()}` : ''}`);
    }
</script>

<svelte:head>
    <title>Balance Sheet Report — Dashboard</title>
</svelte:head>

<PageHeader title="Balance Sheet Report" breadcrumbs={[{ label: 'Reports', href: '/dashboard/reports' }, { label: 'Balance Sheet' }]} />

<div class="space-y-6">
    <div class="grid gap-4 sm:grid-cols-2">
        <Select
            label="Event"
            options={data.events.map((event) => ({ value: event.id, label: event.name }))}
            value={data.selectedEventId}
            placeholder="Select event…"
            onchange={(event) => updateSelection((event.currentTarget as HTMLSelectElement).value)}
        />
        <Select
            label="Show"
            options={data.shows.map((show) => ({ value: show.id, label: show.name }))}
            value={data.selectedShowId}
            placeholder={data.selectedEventId ? 'Select show…' : 'Select event first'}
            disabled={!data.selectedEventId}
            onchange={(event) => updateSelection(data.selectedEventId, (event.currentTarget as HTMLSelectElement).value)}
        />
    </div>

    {#if data.selectedShowId && data.balanceSheet}
        <BalanceSheet
            balanceSheet={data.balanceSheet}
            showName={data.showName}
            clerkUserId={data.actorUserId}
            showId={data.selectedShowId}
            readOnly={true}
        />
    {:else}
        <EmptyState title="Choose a show" body="Select an event and show to inspect expenses." />
    {/if}
</div>
