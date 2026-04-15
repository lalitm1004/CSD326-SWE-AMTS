<script lang="ts">
    import type { TicketDto } from '$lib/types/api/ticket.types';
    import Badge from '$lib/components/ui/Badge.svelte';

    interface Props {
        ticket: TicketDto;
        showName?: string;
        seatLabel?: string;
    }
    let { ticket, showName, seatLabel }: Props = $props();
</script>

<div class="bg-[var(--color-surface)] border border-[var(--color-border)] corner-ornament relative overflow-hidden">
    <!-- Gold strip -->
    <div class="absolute left-0 top-0 bottom-0 w-1 bg-[var(--color-gold)] opacity-60"></div>

    <div class="pl-5 pr-5 py-4 flex items-start justify-between gap-4">
        <div class="flex-1 min-w-0">
            {#if showName}
                <p class="text-[10px] text-[var(--color-text-muted)] uppercase tracking-widest mb-1">{showName}</p>
            {/if}
            <p class="font-['Cormorant_Garamond'] text-lg font-semibold text-[var(--color-text)]">
                {seatLabel ?? `Ticket ${ticket.code.slice(0, 8)}`}
            </p>
            <p class="text-xs font-['DM_Mono'] text-[var(--color-text-muted)] mt-1">{ticket.code}</p>
        </div>
        <div class="shrink-0 flex flex-col items-end gap-2">
            {#if ticket.isRefunded}
                <Badge variant="muted">Refunded</Badge>
            {:else}
                <Badge variant="success">Valid</Badge>
            {/if}
        </div>
    </div>

    <!-- Dashed divider -->
    <div class="mx-5 border-t border-dashed border-[var(--color-border)]"></div>
    <div class="px-5 py-3">
        <p class="text-[10px] text-[var(--color-text-muted)]">Booking #{(ticket.bookingId ?? 'unknown').slice(0, 8)}</p>
    </div>
</div>
