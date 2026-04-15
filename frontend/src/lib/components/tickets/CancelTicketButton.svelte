<script lang="ts">
    import { cancelTickets } from '$lib/services/ticket.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import { toastStore } from '$lib/stores/toast.store';
    import Button from '$lib/components/ui/Button.svelte';
    import ConfirmDialog from '$lib/components/ui/ConfirmDialog.svelte';
    import { invalidateAll } from '$app/navigation';

    interface Props {
        ticketId: string;
        disabled?: boolean;
    }
    let { ticketId, disabled = false }: Props = $props();

    let open = $state(false);
    let loading = $state(false);

    async function handleCancel() {
        loading = true;
        try {
            await cancelTickets({ spectatorUserId: $UserStore!.id, ticketIds: [ticketId] });
            toastStore.add('success', 'Ticket cancelled and refund processed.');
            await invalidateAll();
        } catch (e: unknown) {
            toastStore.add('error', (e as Error).message ?? 'Cancellation failed');
        } finally {
            loading = false;
        }
    }
</script>

<Button variant="danger" size="sm" {disabled} onclick={() => open = true}>Cancel Ticket</Button>

<ConfirmDialog
    bind:open
    title="Cancel Ticket"
    message="Are you sure you want to cancel this ticket? Refund amount depends on how close the show is."
    confirmLabel="Yes, Cancel"
    onconfirm={handleCancel}
/>
