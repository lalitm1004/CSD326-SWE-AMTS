<script lang="ts">
    import { page } from '$app/stores';
    import { rolesStore, hasRole } from '$lib/stores/roles.store';
    import type { RoleEnumT } from '$lib/types/role.type';
    import { Calendar, Users, Gift, Tag, Receipt, BarChart3, LayoutDashboard } from 'lucide-svelte';

    interface NavItem {
        label: string;
        href: string;
        icon: typeof Calendar;
        roles: RoleEnumT[];
    }

    const items: NavItem[] = [
        { label: 'Overview', href: '/dashboard', icon: LayoutDashboard, roles: ['ROOT', 'PRESIDENT', 'AUDITORIUM_SECRETARY', 'SHOW_MANAGER', 'SALES_AGENT', 'FINANCIAL_CLERK'] },
        { label: 'Events', href: '/dashboard/events', icon: Calendar, roles: ['ROOT', 'AUDITORIUM_SECRETARY', 'SHOW_MANAGER'] },
        { label: 'Users', href: '/dashboard/users', icon: Users, roles: ['ROOT', 'PRESIDENT', 'AUDITORIUM_SECRETARY'] },
        { label: 'Complimentary', href: '/dashboard/complimentary', icon: Gift, roles: ['ROOT', 'SHOW_MANAGER', 'AUDITORIUM_SECRETARY'] },
        { label: 'Sales', href: '/dashboard/sales', icon: Tag, roles: ['ROOT', 'SALES_AGENT'] },
        { label: 'Expenses', href: '/dashboard/expenses', icon: Receipt, roles: ['ROOT', 'PRESIDENT', 'FINANCIAL_CLERK'] },
        { label: 'Reports', href: '/dashboard/reports', icon: BarChart3, roles: ['ROOT', 'PRESIDENT'] },
    ];

    const visible = $derived(
        items.filter(item => hasRole($rolesStore, ...item.roles))
    );
</script>

<nav class="w-56 shrink-0 border-r border-[var(--color-border)] bg-[var(--color-canvas)] h-full overflow-y-auto py-6">
    <!-- Logo -->
    <div class="px-5 mb-8">
        <a href="/" class="font-['Cormorant_Garamond'] text-lg font-semibold text-[var(--color-text)] flex items-center gap-2.5">
            <span class="text-[var(--color-gold)] opacity-70">◆</span>
            AMTS
        </a>
        <p class="text-[10px] text-[var(--color-text-muted)] mt-1 uppercase tracking-widest">Management</p>
    </div>

    <div class="flex flex-col gap-0.5 px-3">
        {#each visible as item}
            {@const active = item.href === '/dashboard'
                ? $page.url.pathname === '/dashboard'
                : $page.url.pathname.startsWith(item.href)}
            <a
                href={item.href}
                class="flex items-center gap-3 px-3 py-2.5 text-sm transition-all duration-100 group
                    {active
                        ? 'text-[var(--color-gold)] bg-[var(--color-raised)] border-l-2 border-[var(--color-gold)] -ml-px'
                        : 'text-[var(--color-text-muted)] hover:text-[var(--color-text)] hover:bg-[var(--color-raised)]'}"
            >
                <item.icon size={15} class="shrink-0 {active ? 'text-[var(--color-gold)]' : 'text-inherit'}" />
                {item.label}
            </a>
        {/each}
    </div>
</nav>
