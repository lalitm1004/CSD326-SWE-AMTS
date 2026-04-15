<script lang="ts">
    import { rolesStore, hasRole } from '$lib/stores/roles.store';
    import { UserStore } from '$lib/stores/SupaStore';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import StatCard from '$lib/components/ui/StatCard.svelte';
    import Divider from '$lib/components/ui/Divider.svelte';
    import Badge from '$lib/components/ui/Badge.svelte';
    import type { RoleEnumT } from '$lib/types/role.type';

    const roleLinks: { role: RoleEnumT; label: string; href: string; desc: string }[] = [
        { role: 'AUDITORIUM_SECRETARY', label: 'Manage Events', href: '/dashboard/events', desc: 'Schedule shows, configure seating' },
        { role: 'PRESIDENT', label: 'View Reports', href: '/dashboard/reports', desc: 'Revenue, commissions, balance sheets' },
        { role: 'SHOW_MANAGER', label: 'Your Shows', href: '/dashboard/events', desc: 'Configure and manage your shows' },
        { role: 'SALES_AGENT', label: 'Sell Tickets', href: '/dashboard/sales', desc: 'Process offline ticket sales' },
        { role: 'FINANCIAL_CLERK', label: 'Expenses', href: '/dashboard/expenses', desc: 'Track show expenditures' },
    ];

    const myLinks = $derived(
        roleLinks.filter(l => hasRole($rolesStore, l.role))
    );
</script>

<svelte:head>
    <title>Dashboard — AMTS</title>
</svelte:head>

<PageHeader
    title="Dashboard"
    subtitle="Welcome back, {$UserStore?.email?.split('@')[0] ?? 'there'}"
/>

<Divider />

<div class="mt-8 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
    {#each myLinks as link}
        <a href={link.href} class="group block">
            <div class="corner-ornament bg-[var(--color-surface)] border border-[var(--color-border)] p-5 hover:border-[var(--color-border-gold)] transition-colors">
                <Badge variant={link.role} size="sm">{link.role.replace(/_/g, ' ')}</Badge>
                <p class="font-['Cormorant_Garamond'] text-xl font-semibold text-[var(--color-text)] mt-3 group-hover:text-[var(--color-gold)] transition-colors">
                    {link.label}
                </p>
                <p class="text-sm text-[var(--color-text-muted)] mt-1">{link.desc}</p>
            </div>
        </a>
    {/each}
</div>

{#if myLinks.length === 0}
    <div class="text-center py-16">
        <p class="text-[var(--color-text-muted)] text-sm">No dashboard sections available for your current roles.</p>
    </div>
{/if}
