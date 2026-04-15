<script lang="ts">
    import type { PageData } from './$types';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import EventForm from '$lib/components/events/EventForm.svelte';
    import ShowList from '$lib/components/events/ShowList.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import Divider from '$lib/components/ui/Divider.svelte';
    import Modal from '$lib/components/ui/Modal.svelte';
    import ShowForm from '$lib/components/events/ShowForm.svelte';

    let { data }: { data: PageData } = $props();
    let showAddShow = $state(false);
    let showEditEvent = $state(false);
</script>

<svelte:head>
    <title>{data.event.name} — Dashboard</title>
</svelte:head>

<PageHeader
    title={data.event.name}
    breadcrumbs={[{ label: 'Events', href: '/dashboard/events' }, { label: data.event.name }]}
>
    {#snippet actions()}
        <Button variant="ghost" onclick={() => showEditEvent = true}>Edit</Button>
        <Button variant="primary" onclick={() => showAddShow = true}>+ Add Show</Button>
    {/snippet}
</PageHeader>

{#if data.event.description}
    <p class="text-sm text-[var(--color-text-muted)] mb-6 max-w-2xl">{data.event.description}</p>
{/if}

<Divider label="Shows" />

<div class="mt-6">
    <!-- Override ShowList links to use dashboard paths -->
    <div class="flex flex-col gap-3">
        {#each data.shows as show (show.id)}
            <a href="/dashboard/events/{data.event.id}/shows/{show.id}" class="group flex items-center gap-4 p-5 bg-[var(--color-surface)] border border-[var(--color-border)] hover:border-[var(--color-border-gold)] transition-all">
                <div class="flex-1 min-w-0">
                    <p class="font-['Cormorant_Garamond'] text-lg font-semibold text-[var(--color-text)] group-hover:text-[var(--color-gold)] transition-colors">
                        {show.name}
                    </p>
                    {#if show.startingAt}
                        <p class="text-xs text-[var(--color-text-muted)] mt-0.5">
                            {new Date(show.startingAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                        </p>
                    {/if}
                </div>
                <div class="flex items-center gap-4 text-xs text-[var(--color-text-muted)] shrink-0">
                    <span>₹{show.ordinarySeatPrice ?? '—'} / ₹{show.balconySeatPrice ?? '—'}</span>
                    <svg class="text-[var(--color-gold)] opacity-0 group-hover:opacity-100 transition-opacity" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M5 12h14M12 5l7 7-7 7"/>
                    </svg>
                </div>
            </a>
        {/each}
        {#if data.shows.length === 0}
            <p class="text-center text-[var(--color-text-muted)] py-10 text-sm">No shows yet. Add one to get started.</p>
        {/if}
    </div>
</div>

<!-- Edit event modal -->
<Modal bind:open={showEditEvent} title="Edit Event">
    {#snippet children()}
        <EventForm event={data.event} onSaved={() => showEditEvent = false} />
    {/snippet}
</Modal>

<!-- Add show modal -->
<Modal bind:open={showAddShow} title="Add Show" size="lg">
    {#snippet children()}
        <ShowForm eventId={data.event.id} />
    {/snippet}
</Modal>
