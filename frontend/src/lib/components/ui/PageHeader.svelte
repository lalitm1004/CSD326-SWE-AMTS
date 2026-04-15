<script lang="ts">
    import type { Snippet } from 'svelte';

    interface Crumb {
        label: string;
        href?: string;
    }

    interface Props {
        title: string;
        subtitle?: string;
        breadcrumbs?: Crumb[];
        actions?: Snippet;
    }

    let { title, subtitle, breadcrumbs, actions }: Props = $props();
</script>

<div class="flex items-start justify-between gap-4 mb-8">
    <div>
        {#if breadcrumbs && breadcrumbs.length > 0}
            <nav class="flex items-center gap-1.5 mb-2" aria-label="Breadcrumb">
                {#each breadcrumbs as crumb, i}
                    {#if i > 0}
                        <span class="text-[var(--color-text-muted)] text-xs">/</span>
                    {/if}
                    {#if crumb.href}
                        <a href={crumb.href} class="text-xs text-[var(--color-text-muted)] hover:text-[var(--color-gold)] transition-colors">
                            {crumb.label}
                        </a>
                    {:else}
                        <span class="text-xs text-[var(--color-text-muted)]">{crumb.label}</span>
                    {/if}
                {/each}
            </nav>
        {/if}
        <h1 class="font-['Cormorant_Garamond'] text-3xl font-semibold text-[var(--color-text)] tracking-tight">{title}</h1>
        {#if subtitle}
            <p class="mt-1 text-sm text-[var(--color-text-muted)]">{subtitle}</p>
        {/if}
    </div>
    {#if actions}
        <div class="flex items-center gap-3 shrink-0 mt-1">
            {@render actions()}
        </div>
    {/if}
</div>
