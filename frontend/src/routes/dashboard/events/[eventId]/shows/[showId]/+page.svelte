<script lang="ts">
    import type { PageData } from './$types';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import ShowForm from '$lib/components/events/ShowForm.svelte';
    import Tabs from '$lib/components/ui/Tabs.svelte';
    import SeatDesignationPanel from '$lib/components/seats/SeatDesignationPanel.svelte';
    import Divider from '$lib/components/ui/Divider.svelte';

    let { data }: { data: PageData } = $props();

    const tabs = [
        { id: 'config', label: 'Configuration' },
        { id: 'seats', label: 'Seat Designations' },
        { id: 'sales', label: 'Sales' }
    ];
</script>

<svelte:head>
    <title>{data.show.name} — Dashboard</title>
</svelte:head>

<PageHeader
    title={data.show.name}
    breadcrumbs={[
        { label: 'Events', href: '/dashboard/events' },
        { label: 'Event', href: `/dashboard/events/${data.eventId}` },
        { label: data.show.name }
    ]}
/>

<Tabs {tabs}>
    {#snippet children({ active })}
        {#if active === 'config'}
            <div class="max-w-xl bg-[var(--color-surface)] border border-[var(--color-border)] p-6 corner-ornament">
                <ShowForm eventId={data.eventId} show={data.show} />
            </div>

        {:else if active === 'seats'}
            <SeatDesignationPanel showId={data.show.id} seats={data.seats} />

        {:else if active === 'sales'}
            <p class="text-sm text-[var(--color-text-muted)]">
                View this show's sales: <a href="/dashboard/events/{data.eventId}/shows/{data.show.id}/sales" class="text-[var(--color-gold)] hover:underline">Open sales report →</a>
            </p>
        {/if}
    {/snippet}
</Tabs>
