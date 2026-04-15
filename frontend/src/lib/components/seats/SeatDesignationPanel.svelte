<script lang="ts">
    import type { SeatDto } from '$lib/types/api/seat.types';
    import SeatGrid from './SeatGrid.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import Select from '$lib/components/ui/Select.svelte';
    import { updateSeatDesignation } from '$lib/services/show.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import { toastStore } from '$lib/stores/toast.store';
    import { invalidateAll } from '$app/navigation';

    interface Props {
        showId: string;
        seats: SeatDto[];
    }
    let { showId, seats }: Props = $props();

    let selectedSeats = $state<string[]>([]);
    let designation = $state<'VIP' | 'COMPLIMENTARY' | 'ORDINARY'>('VIP');
    let loading = $state(false);

    async function applyDesignation() {
        if (selectedSeats.length === 0) {
            toastStore.add('warning', 'Select seats first');
            return;
        }
        loading = true;
        try {
            await Promise.all(
                selectedSeats.map(seatId =>
                    updateSeatDesignation(showId, $UserStore!.id, seatId, designation)
                )
            );
            toastStore.add('success', `${selectedSeats.length} seat(s) marked as ${designation}`);
            selectedSeats = [];
            await invalidateAll();
        } catch (e: unknown) {
            toastStore.add('error', (e as Error).message ?? 'Failed to update designation');
        } finally {
            loading = false;
        }
    }
</script>

<div class="flex flex-col gap-6">
    <div class="flex items-end gap-4 flex-wrap">
        <Select
            label="Designation"
            options={[
                { value: 'VIP', label: 'VIP' },
                { value: 'COMPLIMENTARY', label: 'Complimentary' },
                { value: 'ORDINARY', label: 'Ordinary (Reset)' }
            ]}
            bind:value={designation}
        />
        <Button
            variant="primary"
            {loading}
            disabled={selectedSeats.length === 0}
            onclick={applyDesignation}
        >
            Apply to {selectedSeats.length} seat{selectedSeats.length !== 1 ? 's' : ''}
        </Button>
        {#if selectedSeats.length > 0}
            <Button variant="ghost" onclick={() => selectedSeats = []}>Clear selection</Button>
        {/if}
    </div>

    <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-5">
        <SeatGrid
            {seats}
            mode="designate"
            {selectedSeats}
            onSeatToggled={(id) => {
                selectedSeats = selectedSeats.includes(id)
                    ? selectedSeats.filter(s => s !== id)
                    : [...selectedSeats, id];
            }}
        />
    </div>
</div>
