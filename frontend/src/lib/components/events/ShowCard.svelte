<script lang="ts">
    import type { ShowDto } from '$lib/types/api/event.types';

    interface Props {
        show: ShowDto;
        eventId: string;
    }
    let { show, eventId }: Props = $props();

    const formatDate = (s: string | null | undefined) => {
        if (!s) return null;
        return new Date(s).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    };

    const formatPrice = (p: number | null | undefined) =>
        p != null ? `₹${p.toLocaleString('en-IN')}` : 'TBA';
</script>

<a href="/events/{eventId}/shows/{show.id}" class="group flex items-start gap-4 p-5 bg-[var(--color-surface)] border border-[var(--color-border)] hover:border-[var(--color-border-gold)] transition-all">
    {#if show.thumbnailUrl}
        <div class="w-16 h-16 shrink-0 overflow-hidden bg-[var(--color-raised)]">
            <img src={show.thumbnailUrl} alt={show.name} class="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity" />
        </div>
    {:else}
        <div class="w-16 h-16 shrink-0 bg-[var(--color-raised)] flex items-center justify-center text-[var(--color-gold-dim)] opacity-30">◆</div>
    {/if}
    <div class="flex-1 min-w-0">
        <h3 class="font-['Cormorant_Garamond'] text-lg font-semibold text-[var(--color-text)] group-hover:text-[var(--color-gold)] transition-colors truncate">{show.name}</h3>
        {#if show.startingAt}
            <p class="text-xs text-[var(--color-text-muted)] mt-0.5">{formatDate(show.startingAt)}</p>
        {/if}
        <div class="flex items-center gap-4 mt-2">
            <span class="text-xs text-[var(--color-text-muted)]">Ordinary: <span class="text-[var(--color-text)]">{formatPrice(show.ordinarySeatPrice)}</span></span>
            <span class="text-xs text-[var(--color-text-muted)]">Balcony: <span class="text-[var(--color-text)]">{formatPrice(show.balconySeatPrice)}</span></span>
        </div>
    </div>
    <svg class="shrink-0 mt-1 text-[var(--color-gold)] opacity-0 group-hover:opacity-100 transition-opacity" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M5 12h14M12 5l7 7-7 7"/>
    </svg>
</a>
