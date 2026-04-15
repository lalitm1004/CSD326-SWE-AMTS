<script lang="ts">
    import type { PageData } from './$types';
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import SeatGrid from '$lib/components/seats/SeatGrid.svelte';
    import BookingForm from '$lib/components/tickets/BookingForm.svelte';
    import { cartStore } from '$lib/stores/cart.store';
    import { onMount } from 'svelte';

    let { data }: { data: PageData } = $props();

    onMount(() => cartStore.setShow(data.show.id));

    const selectedSeats = $derived($cartStore.selectedSeats);

    function handleSeatToggled(seatId: string) {
        cartStore.toggleSeat(seatId);
    }
</script>

<svelte:head>
    <title>Book Tickets — {data.show.name}</title>
</svelte:head>

<PublicNav />

<div class="max-w-7xl mx-auto px-4 py-8 lg:px-5 xl:px-6">
    <PageHeader
        title="Book Tickets"
        subtitle={data.show.name}
        breadcrumbs={[
            { label: 'Events', href: '/events' },
            { label: 'Event', href: `/events/${data.eventId}` },
            { label: data.show.name, href: `/events/${data.eventId}/shows/${data.show.id}` },
            { label: 'Book' }
        ]}
    />

    <div class="grid grid-cols-1 gap-6 xl:grid-cols-[minmax(0,1fr)_19rem] 2xl:grid-cols-[minmax(0,1fr)_20rem]">
        <!-- Seat grid -->
        <div class="min-w-0">
            <div class="flex items-center gap-4 mb-4">
                <div class="flex-1 h-px bg-[var(--color-border)]"></div>
                <span class="text-[10px] text-[var(--color-gold-dim)] uppercase tracking-widest">Select Your Seats</span>
                <div class="flex-1 h-px bg-[var(--color-border)]"></div>
            </div>
            <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-4 lg:p-4 xl:p-5">
                <SeatGrid
                    seats={data.seats}
                    mode="select"
                    {selectedSeats}
                    onSeatToggled={handleSeatToggled}
                />
            </div>
        </div>

        <!-- Booking form -->
        <div class="min-w-0">
            <div class="flex items-center gap-4 mb-4">
                <div class="flex-1 h-px bg-[var(--color-border)]"></div>
                <span class="text-[10px] text-[var(--color-gold-dim)] uppercase tracking-widest">Booking Summary</span>
                <div class="flex-1 h-px bg-[var(--color-border)]"></div>
            </div>
            <BookingForm show={data.show} seats={data.seats} eventId={data.eventId} />
        </div>
    </div>
</div>
