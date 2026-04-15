<script lang="ts">
    import ExpenseList from '$lib/components/expenses/ExpenseList.svelte';
    import type { BalanceSheetDto } from '$lib/types/api/expense.types';

    interface Props {
        balanceSheet: BalanceSheetDto;
        showName?: string;
        clerkUserId: string;
        showId: string;
        readOnly?: boolean;
    }

    let { balanceSheet, showName = 'Selected show', clerkUserId, showId, readOnly = false }: Props = $props();

    function formatCurrency(value: number): string {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(value);
    }

    const total = $derived(balanceSheet.totalExpenses ?? balanceSheet.expenses.reduce((sum, expense) => sum + (expense.amount ?? 0), 0));
</script>

<section class="space-y-6">
    <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
        <p class="text-[10px] uppercase tracking-widest text-[var(--color-gold-dim)]">Balance Sheet</p>
        <h2 class="mt-2 font-['Cormorant_Garamond'] text-3xl text-[var(--color-text)]">{showName}</h2>
        <p class="mt-1 text-sm text-[var(--color-text-muted)]">{balanceSheet.expenses.length} expense entries</p>

        <div class="mt-5 border-t border-[var(--color-border)] pt-5">
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)]">Total Expenses</p>
            <p class="mt-2 font-['Cormorant_Garamond'] text-4xl text-[var(--color-gold)]">{formatCurrency(total)}</p>
        </div>
    </div>

    <ExpenseList expenses={balanceSheet.expenses} {clerkUserId} {showId} {readOnly} />
</section>
