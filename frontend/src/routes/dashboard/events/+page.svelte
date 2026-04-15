<script lang="ts">
    import type { PageData } from './$types';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import EventCard from '$lib/components/events/EventCard.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import ConfirmDialog from '$lib/components/ui/ConfirmDialog.svelte';
    import { deleteEvent } from '$lib/services/event.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import { toastStore } from '$lib/stores/toast.store';
    import { invalidateAll } from '$app/navigation';

    let { data }: { data: PageData } = $props();

    let deleteTarget = $state<string | null>(null);
    let confirmOpen = $state(false);
    let deleting = $state(false);

    async function handleDelete() {
        if (!deleteTarget) return;
        deleting = true;
        try {
            await deleteEvent(deleteTarget, $UserStore!.id);
            toastStore.add('success', 'Event deleted.');
            await invalidateAll();
        } catch (e: unknown) {
            toastStore.add('error', (e as Error).message ?? 'Delete failed');
        } finally {
            deleting = false;
            deleteTarget = null;
        }
    }
</script>

<svelte:head>
    <title>Events — Dashboard</title>
</svelte:head>

<PageHeader title="Events">
    {#snippet actions()}
        <Button variant="primary" onclick={() => window.location.href = '/dashboard/events/new'}>
            + New Event
        </Button>
    {/snippet}
</PageHeader>

{#if data.events.length === 0}
    <EmptyState title="No events yet" body="Create your first event to get started.">
        {#snippet actions()}
            <Button variant="primary" onclick={() => window.location.href = '/dashboard/events/new'}>
                Create Event
            </Button>
        {/snippet}
    </EmptyState>
{:else}
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        {#each data.events as event (event.id)}
            <div class="relative group">
                <EventCard {event} />
                <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity flex gap-1">
                    <a href="/dashboard/events/{event.id}" class="px-2 py-1 text-[10px] bg-[var(--color-raised)] border border-[var(--color-border)] text-[var(--color-text-muted)] hover:text-[var(--color-text)]">
                        Edit
                    </a>
                    <button
                        onclick={() => { deleteTarget = event.id; confirmOpen = true; }}
                        class="px-2 py-1 text-[10px] bg-[var(--color-raised)] border border-[var(--color-error)]/40 text-[var(--color-error)]"
                    >
                        Delete
                    </button>
                </div>
            </div>
        {/each}
    </div>
{/if}

<ConfirmDialog
    bind:open={confirmOpen}
    title="Delete Event"
    message="This will permanently delete the event and all associated shows. This action cannot be undone."
    confirmLabel="Delete"
    onconfirm={handleDelete}
    oncancel={() => deleteTarget = null}
/>
