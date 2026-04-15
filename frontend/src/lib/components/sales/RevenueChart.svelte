<script lang="ts">
    interface ChartItem {
        label: string;
        value: number;
    }

    interface Props {
        items: ChartItem[];
    }

    let { items }: Props = $props();

    const maxValue = $derived(() => Math.max(...items.map((item) => item.value), 0));

    function formatCurrency(value: number): string {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(value);
    }
</script>

<div class="space-y-3">
    {#each items as item}
        <div class="grid grid-cols-[minmax(0,1fr)_96px] items-center gap-4">
            <div class="min-w-0">
                <div class="mb-1 flex items-center justify-between gap-3">
                    <p class="truncate text-sm text-[var(--color-text)]">{item.label}</p>
                    <p class="font-['DM_Mono'] text-xs text-[var(--color-text-muted)]">
                        {Math.round(maxValue() === 0 ? 0 : (item.value / maxValue()) * 100)}%
                    </p>
                </div>
                <div class="h-3 overflow-hidden border border-[var(--color-border)] bg-[var(--color-raised)]">
                    <div
                        class="h-full bg-gradient-to-r from-[var(--color-gold-dim)] to-[var(--color-gold)] transition-[width] duration-300"
                        style={`width: ${maxValue() === 0 ? 0 : (item.value / maxValue()) * 100}%`}
                    ></div>
                </div>
            </div>
            <p class="text-right font-['DM_Mono'] text-sm text-[var(--color-text)]">{formatCurrency(item.value)}</p>
        </div>
    {/each}
</div>
