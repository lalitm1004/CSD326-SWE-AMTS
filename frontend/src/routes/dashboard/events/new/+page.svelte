<script lang="ts">
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import EventForm from '$lib/components/events/EventForm.svelte';
</script>

<svelte:head>
    <title>New Event — Dashboard</title>
</svelte:head>

<PageHeader
    title="Create Event"
    breadcrumbs={[{ label: 'Events', href: '/dashboard/events' }, { label: 'New' }]}
/>

<div class="max-w-xl bg-[var(--color-surface)] border border-[var(--color-border)] p-6 corner-ornament">
    <EventForm />
</div>
