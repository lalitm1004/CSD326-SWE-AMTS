<script lang="ts">
    import type { Snippet } from 'svelte';
    import { fade, scale } from 'svelte/transition';

    interface Props {
        open: boolean;
        title?: string;
        onclose?: () => void;
        children: Snippet;
        footer?: Snippet;
        size?: 'sm' | 'md' | 'lg';
    }

    let { open = $bindable(), title, onclose, children, footer, size = 'md' }: Props = $props();

    const sizes = {
        sm: 'max-w-sm',
        md: 'max-w-lg',
        lg: 'max-w-2xl'
    };

    function handleBackdrop(e: MouseEvent) {
        if (e.target === e.currentTarget) {
            open = false;
            onclose?.();
        }
    }

    function handleKeydown(e: KeyboardEvent) {
        if (e.key === 'Escape') {
            open = false;
            onclose?.();
        }
    }
</script>

<svelte:window onkeydown={handleKeydown} />

{#if open}
    <div
        class="fixed inset-0 z-50 flex items-center justify-center p-4"
        onclick={handleBackdrop}
        onkeydown={handleKeydown}
        role="dialog"
        aria-modal="true"
        tabindex="-1"
        transition:fade={{ duration: 150 }}
    >
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-black/70 backdrop-blur-sm"></div>

        <!-- Panel -->
        <div
            class="relative w-full {sizes[size]} bg-[var(--color-surface)] border border-[var(--color-border)] shadow-2xl corner-ornament"
            transition:scale={{ duration: 150, start: 0.96 }}
        >
            {#if title}
                <div class="flex items-center justify-between px-6 py-4 border-b border-[var(--color-border)]">
                    <h2 class="font-['Cormorant_Garamond'] text-xl font-semibold text-[var(--color-text)]">{title}</h2>
                    <button
                        onclick={() => { open = false; onclose?.(); }}
                        class="text-[var(--color-text-muted)] hover:text-[var(--color-text)] transition-colors"
                        aria-label="Close"
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M18 6L6 18M6 6l12 12"/>
                        </svg>
                    </button>
                </div>
            {/if}

            <div class="px-6 py-5">
                {@render children()}
            </div>

            {#if footer}
                <div class="px-6 py-4 border-t border-[var(--color-border)] flex justify-end gap-3">
                    {@render footer()}
                </div>
            {/if}
        </div>
    </div>
{/if}
