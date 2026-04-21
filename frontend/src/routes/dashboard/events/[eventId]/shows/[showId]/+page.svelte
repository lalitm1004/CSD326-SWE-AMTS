<script lang="ts">
    import type { PageData } from './$types';
    import { goto } from '$app/navigation';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import ShowForm from '$lib/components/events/ShowForm.svelte';
    import Tabs from '$lib/components/ui/Tabs.svelte';
    import SeatDesignationPanel from '$lib/components/seats/SeatDesignationPanel.svelte';
    import Divider from '$lib/components/ui/Divider.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import ConfirmDialog from '$lib/components/ui/ConfirmDialog.svelte';
    import { deleteShow } from '$lib/services/show.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import { toastStore } from '$lib/stores/toast.store';

    let { data }: { data: PageData } = $props();

    const tabs = [
        { id: 'config', label: 'Configuration' },
        { id: 'seats', label: 'Seat Designations' },
        { id: 'sales', label: 'Sales' }
    ];

    let confirmDeleteOpen = $state(false);
    let deleting = $state(false);

    async function handleDelete() {
        if (deleting || !$UserStore?.id) return;
        deleting = true;

        try {
            await deleteShow(data.show.id, $UserStore.id);
            toastStore.add('success', 'Show deleted.');
            confirmDeleteOpen = false;
            await goto(`/dashboard/events/${data.eventId}`);
        } catch (e: unknown) {
            toastStore.add('error', (e as Error).message ?? 'Delete failed');
            throw e;
        } finally {
            deleting = false;
        }
    }
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
>
    {#snippet actions()}
        <Button variant="danger" onclick={() => confirmDeleteOpen = true}>Delete Show</Button>
    {/snippet}
</PageHeader>

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

<ConfirmDialog
    bind:open={confirmDeleteOpen}
    title="Delete Show"
    message="This will permanently delete this show. This action cannot be undone."
    confirmLabel={deleting ? 'Deleting…' : 'Delete'}
    confirmLoading={deleting}
    onconfirm={handleDelete}
/>
