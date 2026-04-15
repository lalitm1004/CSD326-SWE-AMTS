<script lang="ts">
    import type { Snippet } from 'svelte';
    import type { RoleEnumT } from '$lib/types/role.type';

    type BadgeVariant = 'gold' | 'success' | 'error' | 'warning' | 'info' | 'muted' | RoleEnumT;

    interface Props {
        variant?: BadgeVariant;
        size?: 'sm' | 'md';
        children: Snippet;
    }

    let { variant = 'muted', size = 'sm', children }: Props = $props();

    const roleColors: Partial<Record<RoleEnumT, string>> = {
        ROOT: 'bg-purple-900/40 text-purple-300 border-purple-700/50',
        PRESIDENT: 'bg-amber-900/40 text-amber-300 border-amber-700/50',
        AUDITORIUM_SECRETARY: 'bg-blue-900/40 text-blue-300 border-blue-700/50',
        SHOW_MANAGER: 'bg-teal-900/40 text-teal-300 border-teal-700/50',
        SALES_AGENT: 'bg-green-900/40 text-green-300 border-green-700/50',
        FINANCIAL_CLERK: 'bg-orange-900/40 text-orange-300 border-orange-700/50',
        SPECTATOR: 'bg-[var(--color-raised)] text-[var(--color-text-muted)] border-[var(--color-border)]'
    };

    const variantColors: Record<string, string> = {
        gold: 'bg-[var(--color-gold-dim)]/30 text-[var(--color-gold-light)] border-[var(--color-gold-dim)]',
        success: 'bg-[var(--color-success)]/20 text-[var(--color-success)] border-[var(--color-success)]/40',
        error: 'bg-[var(--color-error)]/20 text-[var(--color-error)] border-[var(--color-error)]/40',
        warning: 'bg-[var(--color-warning)]/20 text-[var(--color-warning)] border-[var(--color-warning)]/40',
        info: 'bg-[var(--color-info)]/20 text-[var(--color-info)] border-[var(--color-info)]/40',
        muted: 'bg-[var(--color-raised)] text-[var(--color-text-muted)] border-[var(--color-border)]'
    };

    const colorClass = $derived(roleColors[variant as RoleEnumT] ?? variantColors[variant] ?? variantColors.muted);
    const sizeClass = $derived(size === 'sm' ? 'text-[10px] px-2 py-0.5' : 'text-xs px-2.5 py-1');
</script>

<span class="inline-flex items-center font-medium border tracking-wide uppercase {colorClass} {sizeClass}">
    {@render children()}
</span>
