<script lang="ts">
    import type { PageData } from './$types';
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import TicketCard from '$lib/components/tickets/TicketCard.svelte';
    import CancelTicketButton from '$lib/components/tickets/CancelTicketButton.svelte';
    import Spinner from '$lib/components/ui/Spinner.svelte';
    import { getTicketsByBooking } from '$lib/services/ticket.service';
    import type { TicketDto } from '$lib/types/api/ticket.types';
    import { onMount } from 'svelte';
    import { page } from '$app/stores';

    let tickets = $state<TicketDto[]>([]);
    let loading = $state(true);

    onMount(async () => {
        try {
            tickets = await getTicketsByBooking($page.params.bookingId ?? '');
        } finally {
            loading = false;
        }
    });
</script>

<svelte:head>
    <title>Booking — AMTS</title>
</svelte:head>

<PublicNav />

<div class="max-w-2xl mx-auto px-6 py-10">
    <PageHeader
        title="Booking Details"
        subtitle={`ID: ${$page.params.bookingId}`}
        breadcrumbs={[{ label: 'Tickets', href: '/tickets' }, { label: 'Booking' }]}
    />

    {#if loading}
        <div class="flex justify-center py-12"><Spinner size="lg" /></div>
    {:else if tickets.length === 0}
        <p class="text-center text-[var(--color-text-muted)] py-12">No tickets found for this booking.</p>
    {:else}
        <div class="flex flex-col gap-4">
            {#each tickets as ticket (ticket.id)}
                <div class="flex flex-col gap-2">
                    <TicketCard {ticket} />
                    {#if !ticket.isRefunded}
                        <div class="flex justify-end">
                            <CancelTicketButton ticketId={ticket.id} />
                        </div>
                    {/if}
                </div>
            {/each}
        </div>
    {/if}
</div>
