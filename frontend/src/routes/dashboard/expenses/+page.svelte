<script lang="ts">
    import { goto } from '$app/navigation';
    import type { PageData } from './$types';
    import BalanceSheet from '$lib/components/expenses/BalanceSheet.svelte';
    import ExpenseForm from '$lib/components/expenses/ExpenseForm.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import Modal from '$lib/components/ui/Modal.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Select from '$lib/components/ui/Select.svelte';

    let { data }: { data: PageData } = $props();

    let createOpen = $state(false);

    function updateSelection(eventId: string, showId = '') {
        const params = new URLSearchParams();
        if (eventId) params.set('eventId', eventId);
        if (showId) params.set('showId', showId);
        goto(`/dashboard/expenses${params.size > 0 ? `?${params.toString()}` : ''}`);
    }
</script>

<svelte:head>
    <title>Expenses — Dashboard</title>
</svelte:head>

<PageHeader title="Expenses" subtitle="Track show-level expenses and review balance sheets.">
    {#snippet actions()}
        {#if data.canEdit && data.selectedShowId}
            <Button variant="primary" onclick={() => createOpen = true}>Add Expense</Button>
        {/if}
    {/snippet}
</PageHeader>

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

    {#if data.balanceSheet && data.selectedShowId}
        <BalanceSheet
            balanceSheet={data.balanceSheet}
            showName={data.showName}
            clerkUserId={data.actorUserId}
            showId={data.selectedShowId}
            readOnly={!data.canEdit}
        />
    {:else}
        <EmptyState title="Choose a show" body="Select an event and show to load the balance sheet." />
    {/if}
    <Modal bind:open={createOpen} title="Add Expense" size="md">
        {#snippet children()}
            <ExpenseForm
                showId={data.selectedShowId}
                clerkUserId={data.actorUserId}
                onSaved={() => { createOpen = false; }}
            />
        {/snippet}
    </Modal>
</div>
