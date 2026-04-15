<script lang="ts">
    import type { PageData } from './$types';
    import ExpenseForm from '$lib/components/expenses/ExpenseForm.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';

    let { data }: { data: PageData } = $props();
</script>

<svelte:head>
    <title>{data.expense.name} — Expense</title>
</svelte:head>

<PageHeader
    title={data.expense.name}
    subtitle={data.showName}
    breadcrumbs={[
        { label: 'Expenses', href: '/dashboard/expenses' },
        { label: data.expense.name }
    ]}
/>

<div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
    <ExpenseForm
        expense={data.expense}
        showId={data.showId}
        clerkUserId={data.actorUserId}
        readOnly={!data.canEdit}
    />
</div>
