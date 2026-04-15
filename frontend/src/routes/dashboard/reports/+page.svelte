<script lang="ts">
    import PageHeader from '$lib/components/ui/PageHeader.svelte';

    const cards = [
        { title: 'Revenue', body: 'Review event and show revenue totals.', href: '/dashboard/reports/revenue' },
        { title: 'Commissions', body: 'Compare agent commissions for a selected event.', href: '/dashboard/reports/commissions' },
        { title: 'Balance Sheets', body: 'Inspect per-show and per-event expenses.', href: '/dashboard/reports/balance-sheet' },
        { title: 'Yearly', body: 'View the consolidated yearly expense picture.', href: '/dashboard/reports/balance-sheet/yearly' }
    ];
</script>

<svelte:head>
    <title>Reports — Dashboard</title>
</svelte:head>

<PageHeader title="Reports" subtitle="President and root financial summaries." />

<div class="grid gap-5 md:grid-cols-2">
    {#each cards as card}
        <a href={card.href} class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-6 transition-colors hover:border-[var(--color-border-gold)] hover:bg-[var(--color-raised)]">
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-gold-dim)]">Report</p>
            <h2 class="mt-2 font-['Cormorant_Garamond'] text-3xl text-[var(--color-text)]">{card.title}</h2>
            <p class="mt-2 text-sm text-[var(--color-text-muted)]">{card.body}</p>
        </a>
    {/each}
</div>
