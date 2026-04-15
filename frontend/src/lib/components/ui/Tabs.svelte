<script lang="ts">
    import type { Snippet } from 'svelte';

    interface Tab {
        id: string;
        label: string;
    }

    interface Props {
        tabs: Tab[];
        active?: string;
        children: Snippet<[{ active: string }]>;
    }

    let { tabs, active = $bindable(tabs[0]?.id ?? ''), children }: Props = $props();
</script>

<div>
    <div class="flex border-b border-[var(--color-border)] mb-6">
        {#each tabs as tab}
            <button
                class="px-5 py-3 text-sm font-medium transition-all duration-150 border-b-2 -mb-px
                    {active === tab.id
                        ? 'text-[var(--color-gold)] border-[var(--color-gold)]'
                        : 'text-[var(--color-text-muted)] border-transparent hover:text-[var(--color-text)]'}"
                onclick={() => active = tab.id}
            >
                {tab.label}
            </button>
        {/each}
    </div>
    {@render children({ active })}
</div>
