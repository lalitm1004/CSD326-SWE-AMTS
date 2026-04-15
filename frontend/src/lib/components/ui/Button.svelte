<script lang="ts">
    import type { Snippet } from 'svelte';

    interface Props {
        variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
        size?: 'sm' | 'md' | 'lg';
        type?: 'button' | 'submit' | 'reset';
        disabled?: boolean;
        loading?: boolean;
        class?: string;
        onclick?: (e: MouseEvent) => void;
        children: Snippet;
    }

    let {
        variant = 'primary',
        size = 'md',
        type = 'button',
        disabled = false,
        loading = false,
        class: className = '',
        onclick,
        children
    }: Props = $props();

    const base = 'inline-flex items-center justify-center gap-2 font-[DM_Sans] font-medium transition-all duration-150 cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed tracking-wide';

    const variants = {
        primary: 'bg-[var(--color-gold)] text-[var(--color-text-on-gold)] hover:bg-[var(--color-gold-light)] border border-[var(--color-gold)]',
        secondary: 'bg-transparent text-[var(--color-gold)] border border-[var(--color-gold)] hover:bg-[var(--color-gold)] hover:text-[var(--color-text-on-gold)]',
        danger: 'bg-transparent text-[var(--color-error)] border border-[var(--color-error)] hover:bg-[var(--color-error)] hover:text-[var(--color-text)]',
        ghost: 'bg-transparent text-[var(--color-text-muted)] border border-transparent hover:text-[var(--color-text)] hover:border-[var(--color-border)]'
    };

    const sizes = {
        sm: 'px-3 py-1.5 text-sm',
        md: 'px-4 py-2 text-sm',
        lg: 'px-6 py-3 text-base'
    };
</script>

<button
    {type}
    disabled={disabled || loading}
    class="{base} {variants[variant]} {sizes[size]} {className}"
    {onclick}
>
    {#if loading}
        <span class="w-3.5 h-3.5 border-2 border-current border-t-transparent rounded-full animate-spin"></span>
    {/if}
    {@render children()}
</button>
