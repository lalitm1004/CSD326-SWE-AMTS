<script lang="ts">
    import { goto } from '$app/navigation';
    import type { PageData } from './$types';
    import ExpenseForm from '$lib/components/expenses/ExpenseForm.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Select from '$lib/components/ui/Select.svelte';

    let { data }: { data: PageData } = $props();

    function updateSelection(eventId: string, showId = '') {
        const params = new URLSearchParams();
        if (eventId) params.set('eventId', eventId);
        if (showId) params.set('showId', showId);
        goto(`/dashboard/expenses/new${params.size > 0 ? `?${params.toString()}` : ''}`);
    }
</script>

<svelte:head>
    <title>New Expense — Dashboard</title>
</svelte:head>

<PageHeader title="New Expense" breadcrumbs={[{ label: 'Expenses', href: '/dashboard/expenses' }, { label: 'New Expense' }]} />

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

    {#if data.canEdit && data.selectedShowId}
        <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
            <ExpenseForm showId={data.selectedShowId} clerkUserId={data.actorUserId} />
        </div>
    {:else}
        <EmptyState title="Select a show to continue" body="Pick a show first, and only clerks or root users can create expenses." />
    {/if}
</div>
