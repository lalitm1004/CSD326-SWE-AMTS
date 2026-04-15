<script lang="ts">
    interface Props {
        label: string;
        value: string | number;
        delta?: string;
        deltaPositive?: boolean;
        icon?: string;
        class?: string;
    }
    let { label, value, delta, deltaPositive, icon, class: className = '' }: Props = $props();
</script>

<div class="corner-ornament bg-[var(--color-surface)] border border-[var(--color-border)] p-5 {className}">
    <div class="flex items-start justify-between gap-3">
        <div>
            <p class="text-[10px] uppercase tracking-widest font-medium text-[var(--color-text-muted)] mb-2">{label}</p>
            <p class="font-['Cormorant_Garamond'] text-3xl font-semibold text-[var(--color-text)]">{value}</p>
            {#if delta}
                <p class="text-xs mt-1.5 {deltaPositive ? 'text-[var(--color-success)]' : 'text-[var(--color-error)]'}">
                    {deltaPositive ? '↑' : '↓'} {delta}
                </p>
            {/if}
        </div>
        {#if icon}
            <div class="text-[var(--color-gold-dim)] opacity-60 shrink-0 mt-0.5">
                {@html icon}
            </div>
        {/if}
    </div>
</div>
