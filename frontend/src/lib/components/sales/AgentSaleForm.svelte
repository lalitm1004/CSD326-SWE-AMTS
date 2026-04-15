<script lang="ts">
    import { goto } from '$app/navigation';
    import Button from '$lib/components/ui/Button.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import Input from '$lib/components/ui/Input.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Select from '$lib/components/ui/Select.svelte';
    import SeatGrid from '$lib/components/seats/SeatGrid.svelte';
    import { purchaseTicketViaAgent } from '$lib/services/ticket.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import { toastStore } from '$lib/stores/toast.store';
    import type { EventDto, ShowDto } from '$lib/types/api/event.types';
    import type { SeatDto } from '$lib/types/api/seat.types';
    import type { TicketDto } from '$lib/types/api/ticket.types';

    interface Props {
        events: EventDto[];
        shows: ShowDto[];
        seats: SeatDto[];
        selectedEventId?: string;
        selectedShowId?: string;
    }

    let {
        events,
        shows,
        seats,
        selectedEventId = '',
        selectedShowId = ''
    }: Props = $props();

    let spectatorUserId = $state('');
    let selectedSeats = $state<string[]>([]);
    let loading = $state(false);
    let result = $state<TicketDto[] | null>(null);

    function updateSelection(eventId: string, showId = '') {
        const params = new URLSearchParams();
        if (eventId) params.set('eventId', eventId);
        if (showId) params.set('showId', showId);
        goto(`/dashboard/sales${params.size > 0 ? `?${params.toString()}` : ''}`);
    }

    async function handleSubmit() {
        if (!spectatorUserId.trim() || !selectedShowId || selectedSeats.length === 0) {
            toastStore.add('warning', 'Select a show, enter a spectator ID, and choose seats.');
            return;
        }

        loading = true;
        try {
            result = await purchaseTicketViaAgent({
                spectatorUserId: spectatorUserId.trim(),
                salesAgentUserId: $UserStore!.id,
                showId: selectedShowId,
                seatIds: selectedSeats
            });
            selectedSeats = [];
            toastStore.add('success', 'Offline sale recorded.');
        } catch (error: unknown) {
            toastStore.add('error', (error as Error).message ?? 'Agent sale failed');
        } finally {
            loading = false;
        }
    }
</script>

<div class="space-y-6">
    <div class="grid gap-4 lg:grid-cols-[minmax(0,20rem)_minmax(0,1fr)]">
        <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
            <div class="space-y-4">
                <Input label="Spectator User ID" bind:value={spectatorUserId} placeholder="UUID" />
                <Select
                    label="Event"
                    options={events.map((event) => ({ value: event.id, label: event.name }))}
                    value={selectedEventId}
                    placeholder="Select event…"
                    onchange={(event) => updateSelection((event.currentTarget as HTMLSelectElement).value)}
                />
                <Select
                    label="Show"
                    options={shows.map((show) => ({ value: show.id, label: show.name }))}
                    value={selectedShowId}
                    placeholder={selectedEventId ? 'Select show…' : 'Select event first'}
                    disabled={!selectedEventId}
                    onchange={(event) => updateSelection(selectedEventId, (event.currentTarget as HTMLSelectElement).value)}
                />

                <div class="border-t border-[var(--color-border)] pt-4">
                    <Button variant="primary" class="w-full" onclick={handleSubmit} {loading} disabled={!selectedShowId || selectedSeats.length === 0}>
                        Confirm Agent Sale
                    </Button>
                </div>
            </div>
        </div>

        <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
            {#if seats.length > 0}
                <SeatGrid
                    {seats}
                    mode="select"
                    {selectedSeats}
                    onSeatToggled={(id) => {
                        selectedSeats = selectedSeats.includes(id)
                            ? selectedSeats.filter((seatId) => seatId !== id)
                            : [...selectedSeats, id];
                    }}
                />
            {:else}
                <EmptyState title="Select a show" body="Choose an event and show to load the seat grid." />
            {/if}
        </div>
    </div>

    {#if result}
        <section class="corner-ornament border border-[var(--color-success)]/40 bg-[var(--color-surface)] p-5">
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-success)]">Sale Confirmed</p>
            <h2 class="mt-2 font-['Cormorant_Garamond'] text-3xl text-[var(--color-text)]">Booking ID: {result[0]?.bookingId}</h2>
            <p class="mt-2 text-sm text-[var(--color-text-muted)]">
                Created {result.length} ticket{result.length === 1 ? '' : 's'}.
                First ticket code: <span class="font-['DM_Mono'] text-[var(--color-text)]">{result[0]?.code}</span>
            </p>
        </section>
    {/if}
</div>
