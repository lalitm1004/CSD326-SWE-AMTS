<script lang="ts">
    import { page } from '$app/stores';
    import { rolesStore } from '$lib/stores/roles.store';
    import { SessionStore } from '$lib/stores/SupaStore';

    const adminRoles = ['ROOT', 'PRESIDENT', 'AUDITORIUM_SECRETARY', 'SHOW_MANAGER', 'SALES_AGENT', 'FINANCIAL_CLERK'];
    const isAdmin = $derived($rolesStore.some(r => adminRoles.includes(r)));
    const isLoggedIn = $derived(!!$SessionStore);

    const links = $derived([
        { href: '/events', label: 'Events' },
        { href: '/tickets', label: 'My Tickets' },
        ...($SessionStore ? [{ href: '/coupons', label: 'Coupons' }] : []),
    ]);
</script>

<header class="fixed top-0 left-0 right-0 z-40 border-b border-[var(--color-border)] bg-[var(--color-canvas)]/90 backdrop-blur-md">
    <div class="max-w-7xl mx-auto px-6 h-14 flex items-center justify-between gap-8">
        <!-- Wordmark -->
        <a href="/" class="font-['Cormorant_Garamond'] text-xl font-semibold text-[var(--color-text)] tracking-wide hover:text-[var(--color-gold)] transition-colors flex items-center gap-3">
            <span class="text-[var(--color-gold)] opacity-60 text-sm">◆</span>
            AMTS
        </a>

        <!-- Nav links -->
        <nav class="hidden md:flex items-center gap-1">
            {#each links as link}
                <a
                    href={link.href}
                    class="px-3 py-1.5 text-sm transition-colors
                        {$page.url.pathname.startsWith(link.href)
                            ? 'text-[var(--color-gold)]'
                            : 'text-[var(--color-text-muted)] hover:text-[var(--color-text)]'}"
                >
                    {link.label}
                </a>
            {/each}
        </nav>

        <!-- Right -->
        <div class="flex items-center gap-3">
            {#if isAdmin}
                <a href="/dashboard" class="text-xs text-[var(--color-gold-dim)] hover:text-[var(--color-gold)] transition-colors uppercase tracking-widest">
                    Dashboard
                </a>
            {/if}
            {#if isLoggedIn}
                <a href="/profile" class="text-sm text-[var(--color-text-muted)] hover:text-[var(--color-text)] transition-colors">
                    Profile
                </a>
            {:else}
                <a href="/auth" class="text-sm px-3 py-1.5 border border-[var(--color-gold)] text-[var(--color-gold)] hover:bg-[var(--color-gold)] hover:text-[var(--color-text-on-gold)] transition-all">
                    Sign In
                </a>
            {/if}
        </div>
    </div>
</header>

<!-- Spacer -->
<div class="h-14"></div>
