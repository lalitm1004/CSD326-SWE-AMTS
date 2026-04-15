<script lang="ts">
    import type { EventDto } from '$lib/types/api/event.types';

    interface Props {
        event: EventDto;
    }
    let { event }: Props = $props();

    const formatDate = (s: string | null | undefined) => {
        if (!s) return null;
        return new Date(s).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    const dateRange = $derived(() => {
        const s = formatDate(event.startingAt);
        const e = formatDate(event.endingAt);
        if (s && e && s !== e) return `${s} — ${e}`;
        return s ?? e ?? 'Dates TBA';
    });
</script>

<a href="/events/{event.id}" class="group block corner-ornament bg-[var(--color-surface)] border border-[var(--color-border)] hover:border-[var(--color-border-gold)] transition-all duration-200">
    <!-- Thumbnail -->
    {#if event.thumbnailUrl}
        <div class="aspect-video overflow-hidden bg-[var(--color-raised)]">
            <img
                src={event.thumbnailUrl}
                alt={event.name}
                class="w-full h-full object-cover opacity-80 group-hover:opacity-100 group-hover:scale-[1.02] transition-all duration-300"
            />
        </div>
    {:else}
        <div class="aspect-video bg-[var(--color-raised)] flex items-center justify-center">
            <span class="text-[var(--color-gold-dim)] opacity-30 text-4xl font-['Cormorant_Garamond']">◆</span>
        </div>
    {/if}

    <!-- Content -->
    <div class="p-5">
        <p class="text-[10px] text-[var(--color-text-muted)] uppercase tracking-widest mb-2">{dateRange()}</p>
        <h3 class="font-['Cormorant_Garamond'] text-xl font-semibold text-[var(--color-text)] group-hover:text-[var(--color-gold)] transition-colors leading-tight">
            {event.name}
        </h3>
        {#if event.description}
            <p class="text-sm text-[var(--color-text-muted)] mt-2 line-clamp-2">{event.description}</p>
        {/if}
        <div class="flex items-center gap-1.5 mt-4 text-xs text-[var(--color-gold)] group-hover:gap-2.5 transition-all">
            <span>View shows</span>
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
        </div>
    </div>
</a>
