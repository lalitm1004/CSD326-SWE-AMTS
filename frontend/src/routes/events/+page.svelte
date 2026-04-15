<script lang="ts">
    import type { PageData } from './$types';
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import EventGrid from '$lib/components/events/EventGrid.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Spinner from '$lib/components/ui/Spinner.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';

    let { data }: { data: PageData } = $props();
</script>

<svelte:head>
    <title>Events — AMTS</title>
</svelte:head>

<PublicNav />

<div class="max-w-7xl mx-auto px-6 py-10">
    <PageHeader title="All Events" subtitle="Browse and book tickets for upcoming shows" />

    {#if data.loading}
        <div class="flex justify-center py-16"><Spinner size="lg" /></div>
    {:else if data.error}
        <p class="text-center text-[var(--color-text-muted)] py-16">{data.error}</p>
    {:else if data.events.length === 0}
        <EmptyState title="No events found" body="There are no events scheduled at the moment." />
    {:else}
        <EventGrid events={data.events} />
    {/if}
</div>
