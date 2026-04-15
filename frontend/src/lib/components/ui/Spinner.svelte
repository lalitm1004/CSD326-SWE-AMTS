<script lang="ts">
    interface Props {
        size?: 'sm' | 'md' | 'lg';
        class?: string;
    }
    let { size = 'md', class: className = '' }: Props = $props();
    const sizes = { sm: 'w-4 h-4', md: 'w-6 h-6', lg: 'w-8 h-8' };
</script>

<span
    class="inline-block rounded-full border-2 border-[var(--color-border)] border-t-[var(--color-gold)] animate-spin {sizes[size]} {className}"
    role="status"
    aria-label="Loading"
></span>
