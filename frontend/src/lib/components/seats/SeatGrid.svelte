<script lang="ts">
    import type { SeatDto } from '$lib/types/api/seat.types';
    import SeatLegend from './SeatLegend.svelte';

    type GridMode = 'view' | 'select' | 'designate' | 'complimentary';

    interface Props {
        seats: SeatDto[];
        mode?: GridMode;
        selectedSeats?: string[];
        onSeatToggled?: (seatId: string) => void;
    }

    let { seats, mode = 'view', selectedSeats = [], onSeatToggled }: Props = $props();

    // Group seats by row, sorted
    const seatsByRow = $derived(() => {
        const map = new Map<string, SeatDto[]>();
        for (const seat of seats) {
            const row = seat.row ?? '?';
            if (!map.has(row)) map.set(row, []);
            map.get(row)!.push(seat);
        }
        // Sort each row by col
        for (const [, rowSeats] of map) {
            rowSeats.sort((a, b) => (a.col ?? 0) - (b.col ?? 0));
        }
        // Sort rows alphabetically
        return Array.from(map.entries()).sort(([a], [b]) => a.localeCompare(b));
    });

    // Separate balcony rows (A–E) from ordinary (F–T)
    const BALCONY_ROWS = ['A','B','C','D','E'];
    const balconyRows = $derived(seatsByRow().filter(([r]) => BALCONY_ROWS.includes(r)));
    const ordinaryRows = $derived(seatsByRow().filter(([r]) => !BALCONY_ROWS.includes(r)));

    function isSelectableInMode(seat: SeatDto): boolean {
        if (mode === 'view') return false;
        if (mode === 'select') {
            return seat.isAvailable !== false && (seat.designation ?? 'ORDINARY') === 'ORDINARY';
        }
        if (mode === 'complimentary') {
            return seat.isAvailable !== false;
        }
        return true;
    }

    function getSeatColor(seat: SeatDto, isSelected: boolean): string {
        if (isSelected) return 'bg-[var(--color-gold)] border-[var(--color-gold)] text-[var(--color-text-on-gold)]';
        if (seat.isAvailable === false) return 'bg-[var(--color-raised)] border-[var(--color-border)] text-[var(--color-text-muted)] opacity-40 cursor-not-allowed';
        if (mode === 'select' && seat.designation === 'VIP') return 'bg-amber-900/40 border-amber-600/50 text-amber-300 opacity-60 cursor-not-allowed';
        if (mode === 'select' && seat.designation === 'COMPLIMENTARY') return 'bg-purple-900/40 border-purple-600/50 text-purple-300 opacity-60 cursor-not-allowed';
        if (seat.designation === 'VIP') return 'bg-amber-900/40 border-amber-600/50 text-amber-300';
        if (seat.designation === 'COMPLIMENTARY') return 'bg-purple-900/40 border-purple-600/50 text-purple-300';
        if (seat.type === 'BALCONY') return 'bg-blue-950/60 border-blue-800/50 text-blue-300 hover:border-[var(--color-gold)] hover:bg-[var(--color-gold-dim)]/30';
        return 'bg-[var(--color-raised)] border-[var(--color-border)] text-[var(--color-text-muted)] hover:border-[var(--color-gold)] hover:text-[var(--color-gold)]';
    }

    function handleSeatClick(seat: SeatDto) {
        if (!isSelectableInMode(seat)) return;
        onSeatToggled?.(seat.id);
    }

    const isSelected = (seatId: string) => selectedSeats.includes(seatId);
    const getSeatLabel = (seat: SeatDto) => {
        const bookingLabel = seat.isAvailable === false
            ? 'Booked'
            : seat.designation ?? 'ORDINARY';
        return `${seat.row}${seat.col} · ${seat.type} · ${bookingLabel}`;
    };
</script>

<div class="w-full overflow-x-auto">
    <!-- Stage indicator -->
    <div class="flex justify-center mb-6">
        <div class="px-12 py-2 border border-[var(--color-border-gold)] opacity-60 text-[10px] text-[var(--color-gold-dim)] uppercase tracking-[0.4em] relative">
            Stage
            <div class="absolute -bottom-px left-0 right-0 h-px bg-[var(--color-border-gold)] opacity-40"></div>
        </div>
    </div>

    <!-- Balcony section -->
    {#if balconyRows.length > 0}
        <div class="mb-2">
            <p class="text-[10px] text-[var(--color-text-muted)] uppercase tracking-widest text-center mb-3 opacity-60">Balcony</p>
            <div class="flex flex-col gap-1 items-center min-w-max">
                {#each balconyRows as [rowLabel, rowSeats]}
                    <div class="flex items-center gap-1">
                        <span class="w-5 text-[9px] text-[var(--color-text-muted)] text-right font-['DM_Mono'] opacity-60">{rowLabel}</span>
                        <div class="flex gap-0.5">
                            {#each rowSeats as seat}
                                {@const selected = isSelected(seat.id)}
                                <button
                                    class="w-5 h-5 text-[7px] border transition-all duration-100 font-['DM_Mono'] {getSeatColor(seat, selected)}
                                        {isSelectableInMode(seat) ? 'cursor-pointer' : 'cursor-default'}"
                                    onclick={() => handleSeatClick(seat)}
                                    title={getSeatLabel(seat)}
                                    disabled={mode === 'view'}
                                >
                                    {seat.col}
                                </button>
                            {/each}
                        </div>
                    </div>
                {/each}
            </div>
        </div>

        <!-- Section divider -->
        <div class="flex items-center gap-3 my-4">
            <div class="flex-1 h-px bg-[var(--color-border-gold)] opacity-20"></div>
            <span class="text-[9px] text-[var(--color-gold-dim)] opacity-50 tracking-widest uppercase">◆ Ordinary Section ◆</span>
            <div class="flex-1 h-px bg-[var(--color-border-gold)] opacity-20"></div>
        </div>
    {/if}

    <!-- Ordinary section -->
    {#if ordinaryRows.length > 0}
        <div class="flex flex-col gap-1 items-center min-w-max">
            {#each ordinaryRows as [rowLabel, rowSeats]}
                <div class="flex items-center gap-1">
                    <span class="w-5 text-[9px] text-[var(--color-text-muted)] text-right font-['DM_Mono'] opacity-60">{rowLabel}</span>
                    <div class="flex gap-0.5">
                        {#each rowSeats as seat}
                            {@const selected = isSelected(seat.id)}
                            <button
                                class="w-5 h-5 text-[7px] border transition-all duration-100 font-['DM_Mono'] {getSeatColor(seat, selected)}
                                    {isSelectableInMode(seat) ? 'cursor-pointer' : 'cursor-default'}"
                                onclick={() => handleSeatClick(seat)}
                                title={getSeatLabel(seat)}
                                disabled={mode === 'view'}
                            >
                                {seat.col}
                            </button>
                        {/each}
                    </div>
                </div>
            {/each}
        </div>
    {/if}

    <div class="mt-6">
        <SeatLegend {mode} />
    </div>
</div>
