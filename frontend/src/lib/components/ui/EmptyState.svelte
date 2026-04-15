<script lang="ts">
    import type { Snippet } from 'svelte';

    interface Props {
        title: string;
        body?: string;
        actions?: Snippet;
    }
    let { title, body, actions }: Props = $props();
</script>

<div class="flex flex-col items-center justify-center py-20 px-6 text-center">
    <div class="mb-4 text-[var(--color-gold-dim)] opacity-40">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
            <rect x="3" y="3" width="18" height="18" rx="0"/>
            <path d="M9 9h6M9 12h6M9 15h4"/>
        </svg>
    </div>
    <div class="w-8 h-px bg-[var(--color-border-gold)] opacity-40 mb-4"></div>
    <h3 class="font-['Cormorant_Garamond'] text-2xl font-medium text-[var(--color-text)] mb-2">{title}</h3>
    {#if body}
        <p class="text-sm text-[var(--color-text-muted)] max-w-sm">{body}</p>
    {/if}
    {#if actions}
        <div class="mt-6">
            {@render actions()}
        </div>
    {/if}
</div>
