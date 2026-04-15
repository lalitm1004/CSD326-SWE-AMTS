<script lang="ts">
    import { fly } from 'svelte/transition';
    import { toastStore, type Toast } from '$lib/stores/toast.store';

    interface Props {
        toast: Toast;
    }

    let { toast }: Props = $props();

    const icons = {
        success: `<path d="M20 6L9 17l-5-5" stroke-linecap="round" stroke-linejoin="round"/>`,
        error: `<path d="M18 6L6 18M6 6l12 12" stroke-linecap="round" stroke-linejoin="round"/>`,
        warning: `<path d="M12 9v4m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>`,
        info: `<circle cx="12" cy="12" r="10"/><path d="M12 16v-4m0-4h.01"/>`
    };

    const colors = {
        success: 'border-[var(--color-success)] text-[var(--color-success)]',
        error: 'border-[var(--color-error)] text-[var(--color-error)]',
        warning: 'border-[var(--color-warning)] text-[var(--color-warning)]',
        info: 'border-[var(--color-info)] text-[var(--color-info)]'
    };
</script>

<div
    class="flex items-start gap-3 px-4 py-3 bg-[var(--color-surface)] border-l-2 shadow-xl min-w-72 max-w-sm {colors[toast.type]}"
    transition:fly={{ x: 20, duration: 200 }}
>
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="shrink-0 mt-0.5">
        {@html icons[toast.type]}
    </svg>
    <p class="text-sm text-[var(--color-text)] flex-1">{toast.message}</p>
    <button
        onclick={() => toastStore.dismiss(toast.id)}
        class="text-[var(--color-text-muted)] hover:text-[var(--color-text)] transition-colors shrink-0"
        aria-label="Dismiss notification"
    >
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
        </svg>
    </button>
</div>
