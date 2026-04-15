<script lang="ts">
    import type { ShowDto } from '$lib/types/api/event.types';
    import type { SeatDto } from '$lib/types/api/seat.types';
    import { cartStore } from '$lib/stores/cart.store';
    import { UserStore } from '$lib/stores/SupaStore';
    import { purchaseTicket } from '$lib/services/ticket.service';
    import { toastStore } from '$lib/stores/toast.store';
    import { goto } from '$app/navigation';
    import Button from '$lib/components/ui/Button.svelte';
    import Input from '$lib/components/ui/Input.svelte';

    interface Props {
        show: ShowDto;
        seats: SeatDto[];
        eventId: string;
    }
    let { show, seats, eventId }: Props = $props();

    let couponCode = $state('');
    let loading = $state(false);

    const selectedIds = $derived($cartStore.selectedSeats);
    const selectedSeats = $derived(
        selectedIds.map(id => seats.find(s => s.id === id)).filter(Boolean) as SeatDto[]
    );

    const total = $derived(() => {
        let sum = 0;
        for (const seat of selectedSeats) {
            const price = seat.type === 'BALCONY'
                ? (show.balconySeatPrice ?? 0)
                : (show.ordinarySeatPrice ?? 0);
            sum += price;
        }
        // Apply 10% coupon discount
        if (couponCode.trim()) sum *= 0.9;
        return sum;
    });

    async function handleBook() {
        if (selectedIds.length === 0) {
            toastStore.add('warning', 'Please select at least one seat');
            return;
        }
        loading = true;
        try {
            const tickets = await purchaseTicket({
                spectatorUserId: $UserStore!.id,
                showId: show.id,
                seatIds: selectedIds,
                couponCode: couponCode.trim() || undefined
            });
            cartStore.clear();
            toastStore.add('success', 'Booking confirmed!');
            goto(`/tickets/${tickets[0]?.bookingId}`);
        } catch (e: unknown) {
            toastStore.add('error', (e as Error).message ?? 'Booking failed');
        } finally {
            loading = false;
        }
    }
</script>

<div class="sticky top-24 bg-[var(--color-surface)] border border-[var(--color-border)] p-5 corner-ornament">
    <!-- Selected seats list -->
    {#if selectedSeats.length === 0}
        <p class="text-sm text-[var(--color-text-muted)] text-center py-4">No seats selected</p>
    {:else}
        <div class="flex flex-col gap-2 mb-4">
            {#each selectedSeats as seat}
                <div class="flex items-center justify-between text-sm">
                    <span class="text-[var(--color-text)] font-['DM_Mono'] text-xs">
                        Row {seat.row}, Seat {seat.col}
                        <span class="text-[var(--color-text-muted)] ml-1">({seat.type})</span>
                    </span>
                    <span class="text-[var(--color-gold)]">
                        ₹{seat.type === 'BALCONY'
                            ? (show.balconySeatPrice ?? 0)
                            : (show.ordinarySeatPrice ?? 0)}
                    </span>
                </div>
            {/each}
        </div>

        <div class="h-px bg-[var(--color-border)] my-3"></div>
    {/if}

    <!-- Coupon -->
    <Input
        label="Coupon Code (optional)"
        placeholder="10-digit code"
        bind:value={couponCode}
    />

    {#if selectedSeats.length > 0}
        <div class="flex items-center justify-between mt-4 mb-5">
            <span class="text-sm text-[var(--color-text-muted)]">Total</span>
            <span class="font-['Cormorant_Garamond'] text-2xl text-[var(--color-gold)]">
                ₹{total().toFixed(0)}
            </span>
        </div>
        {#if couponCode.trim()}
            <p class="text-[10px] text-[var(--color-success)] mb-3">◆ 10% coupon discount applied</p>
        {/if}
    {/if}

    <Button
        variant="primary"
        class="w-full mt-2"
        {loading}
        disabled={selectedIds.length === 0}
        onclick={handleBook}
    >
        Confirm Booking
    </Button>

    <p class="text-[10px] text-[var(--color-text-muted)] text-center mt-3">
        Cancellation policy applies · Non-refundable within 24h
    </p>
</div>
