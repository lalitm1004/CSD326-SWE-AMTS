<script lang="ts">
    interface Props {
        label?: string;
        class?: string;
    }
    let { label, class: className = '' }: Props = $props();
</script>

<div class="flex items-center gap-4 my-2 {className}">
    <div class="flex-1 h-px bg-[var(--color-border-gold)] opacity-40"></div>
    {#if label}
        <span class="text-[10px] font-medium text-[var(--color-gold-dim)] uppercase tracking-widest px-1">
            {label}
        </span>
        <div class="flex-1 h-px bg-[var(--color-border-gold)] opacity-40"></div>
    {:else}
        <span class="text-[var(--color-gold-dim)] text-xs opacity-60">◆</span>
        <div class="flex-1 h-px bg-[var(--color-border-gold)] opacity-40"></div>
    {/if}
</div>
