<script lang="ts">
    import { goto } from '$app/navigation';
    import type { PageData } from './$types';
    import BalanceSheet from '$lib/components/expenses/BalanceSheet.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Select from '$lib/components/ui/Select.svelte';

    let { data }: { data: PageData } = $props();
</script>

<svelte:head>
    <title>Event Balance Sheets — Dashboard</title>
</svelte:head>

<PageHeader
    title="Balance Sheets By Event"
    breadcrumbs={[
        { label: 'Reports', href: '/dashboard/reports' },
        { label: 'By Event' }
    ]}
/>

<div class="space-y-6">
    <div class="max-w-md">
        <Select
            label="Event"
            options={data.events.map((event) => ({ value: event.id, label: event.name }))}
            value={data.selectedEventId}
            placeholder="Select event…"
            onchange={(event) => {
                const eventId = (event.currentTarget as HTMLSelectElement).value;
                goto(`/dashboard/reports/balance-sheet/by-event${eventId ? `?eventId=${eventId}` : ''}`);
            }}
        />
    </div>

    {#if data.selectedEventId}
        <div class="space-y-6">
            {#each data.sheets as sheet}
                <BalanceSheet
                    balanceSheet={sheet}
                    showName={sheet.showId ? (data.showNames[sheet.showId] ?? sheet.showId) : 'Unknown show'}
                    clerkUserId={data.actorUserId}
                    showId={sheet.showId ?? ''}
                    readOnly={true}
                />
            {/each}
        </div>
    {:else}
        <EmptyState title="Choose an event" body="Select an event to view every show-level balance sheet." />
    {/if}
</div>
