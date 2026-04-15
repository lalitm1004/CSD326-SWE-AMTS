<script lang="ts">
    import type { PageData } from './$types';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import AgentSaleForm from '$lib/components/sales/AgentSaleForm.svelte';

    let { data }: { data: PageData } = $props();
</script>

<svelte:head>
    <title>Sales — Dashboard</title>
</svelte:head>

<PageHeader title="Sales" subtitle="Record offline seat sales for spectators." />

<AgentSaleForm
    events={data.events}
    shows={data.shows}
    seats={data.seats}
    selectedEventId={data.selectedEventId}
    selectedShowId={data.selectedShowId}
/>
