<script lang="ts">
    interface Props {
        mode?: 'view' | 'select' | 'designate' | 'complimentary';
    }
    let { mode = 'view' }: Props = $props();

    const items = $derived([
        { color: 'bg-[var(--color-raised)] border-[var(--color-border)]', label: 'Available (Ordinary)' },
        { color: 'bg-blue-950/60 border-blue-800/50', label: 'Available (Balcony)' },
        { color: 'bg-[var(--color-gold)] border-[var(--color-gold)]', label: mode === 'designate' ? 'Selected' : 'Your Selection', hidden: mode === 'view' },
        { color: 'bg-amber-900/40 border-amber-600/50', label: 'VIP' },
        { color: 'bg-purple-900/40 border-purple-600/50', label: 'Complimentary' },
        { color: 'bg-[var(--color-raised)] border-[var(--color-border)] opacity-40', label: 'Sold / Unavailable' }
    ]);
</script>

<div class="flex flex-wrap justify-center gap-x-5 gap-y-2">
    {#each items.filter(i => !i.hidden) as item}
        <div class="flex items-center gap-1.5">
            <span class="w-3.5 h-3.5 border {item.color} inline-block shrink-0"></span>
            <span class="text-[10px] text-[var(--color-text-muted)]">{item.label}</span>
        </div>
    {/each}
</div>
