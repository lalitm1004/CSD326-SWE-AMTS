<script lang="ts">
    import { invalidateAll } from '$app/navigation';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import Select from '$lib/components/ui/Select.svelte';
    import SeatGrid from '$lib/components/seats/SeatGrid.svelte';
    import { getAllEvents, getShowsByEvent } from '$lib/services/event.service';
    import { getShowSeats, updateSeatDesignation } from '$lib/services/show.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import { toastStore } from '$lib/stores/toast.store';
    import type { EventDto, ShowDto } from '$lib/types/api/event.types';
    import type { SeatDto } from '$lib/types/api/seat.types';
    import { onMount } from 'svelte';

    let events = $state<EventDto[]>([]);
    let shows = $state<ShowDto[]>([]);
    let seats = $state<SeatDto[]>([]);
    let selectedEventId = $state('');
    let selectedShowId = $state('');
    let selectedSeats = $state<string[]>([]);
    let loading = $state(false);
    const complimentarySeatCount = $derived(seats.filter((seat) => seat.designation === 'COMPLIMENTARY').length);

    onMount(async () => {
        try { events = await getAllEvents(); } catch {}
    });

    $effect(() => {
        if (selectedEventId) {
            shows = []; selectedShowId = ''; seats = []; selectedSeats = [];
            getShowsByEvent(selectedEventId).then(s => shows = s).catch(() => {});
        }
    });

    $effect(() => {
        if (selectedShowId) {
            seats = []; selectedSeats = [];
            getShowSeats(selectedShowId).then(s => seats = s).catch(() => {});
        }
    });

    async function applyDesignation(designation: 'COMPLIMENTARY' | 'ORDINARY') {
        if (selectedSeats.length === 0 || !selectedShowId) {
            toastStore.add('warning', 'Select a show and at least one seat');
            return;
        }
        loading = true;
        try {
            await Promise.all(
                selectedSeats.map((seatId) =>
                    updateSeatDesignation(selectedShowId, $UserStore!.id, seatId, designation)
                )
            );
            toastStore.add(
                'success',
                designation === 'COMPLIMENTARY'
                    ? `${selectedSeats.length} seat(s) marked as complimentary.`
                    : `${selectedSeats.length} seat(s) reset to ordinary.`
            );
            selectedSeats = [];
            await invalidateAll();
            seats = await getShowSeats(selectedShowId);
        } catch (e: unknown) {
            toastStore.add('error', (e as Error).message ?? 'Failed to update complimentary seats');
        } finally {
            loading = false;
        }
    }
</script>

<svelte:head>
    <title>Complimentary Seats — Dashboard</title>
</svelte:head>

<PageHeader
    title="Complimentary Seats"
    subtitle="Mark seats as complimentary during show configuration so they remain excluded from normal sale."
/>

<div class="flex flex-col gap-6">
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 max-w-xl">
        <Select
            label="Event"
            options={events.map(e => ({ value: e.id, label: e.name }))}
            bind:value={selectedEventId}
            placeholder="Select event…"
        />
        <Select
            label="Show"
            options={shows.map(s => ({ value: s.id, label: s.name }))}
            bind:value={selectedShowId}
            placeholder={selectedEventId ? 'Select show…' : 'Select event first'}
            disabled={!selectedEventId}
        />
    </div>

    {#if seats.length > 0}
        <div>
            <div class="flex items-center gap-4 mb-4">
                <Button
                    variant="primary"
                    {loading}
                    disabled={selectedSeats.length === 0}
                    onclick={() => applyDesignation('COMPLIMENTARY')}
                >
                    Mark {selectedSeats.length} Seat{selectedSeats.length !== 1 ? 's' : ''} Complimentary
                </Button>
                <Button
                    variant="secondary"
                    {loading}
                    disabled={selectedSeats.length === 0}
                    onclick={() => applyDesignation('ORDINARY')}
                >
                    Reset to Ordinary
                </Button>
                {#if selectedSeats.length > 0}
                    <Button variant="ghost" onclick={() => selectedSeats = []}>Clear</Button>
                {/if}
            </div>
            <p class="mb-4 text-sm text-[var(--color-text-muted)]">
                Complimentary seats configured for this show: <span class="font-['DM_Mono'] text-[var(--color-gold)]">{complimentarySeatCount}</span>
            </p>
            <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-5">
                <SeatGrid
                    {seats}
                    mode="complimentary"
                    {selectedSeats}
                    onSeatToggled={(id) => {
                        selectedSeats = selectedSeats.includes(id)
                            ? selectedSeats.filter(s => s !== id)
                            : [...selectedSeats, id];
                    }}
                />
            </div>
        </div>
    {/if}
</div>
