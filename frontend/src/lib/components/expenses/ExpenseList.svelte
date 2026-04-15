<script lang="ts">
    import { invalidateAll } from '$app/navigation';
    import Button from '$lib/components/ui/Button.svelte';
    import ConfirmDialog from '$lib/components/ui/ConfirmDialog.svelte';
    import DataTable from '$lib/components/ui/DataTable.svelte';
    import Modal from '$lib/components/ui/Modal.svelte';
    import { deleteExpense } from '$lib/services/expense.service';
    import { toastStore } from '$lib/stores/toast.store';
    import type { ExpenseDto } from '$lib/types/api/expense.types';
    import ExpenseForm from './ExpenseForm.svelte';

    interface Props {
        expenses: ExpenseDto[];
        clerkUserId: string;
        showId: string;
        readOnly?: boolean;
        onDeleted?: () => void;
    }

    const columns = [
        { key: 'name', label: 'Name', sortable: true },
        { key: 'description', label: 'Description' },
        { key: 'amount', label: 'Amount', sortable: true },
        { key: 'date', label: 'Date', sortable: true },
        { key: 'actions', label: 'Actions' }
    ];

    let { expenses, clerkUserId, showId, readOnly = false, onDeleted }: Props = $props();

    let editExpense = $state<ExpenseDto | null>(null);
    let editOpen = $state(false);
    let deleteTarget = $state<ExpenseDto | null>(null);
    let confirmOpen = $state(false);
    let deleting = $state(false);

    function formatCurrency(value: number): string {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(value);
    }

    async function handleDelete() {
        if (!deleteTarget) return;
        deleting = true;
        try {
            await deleteExpense(clerkUserId, deleteTarget.id);
            toastStore.add('success', 'Expense deleted.');
            await invalidateAll();
            onDeleted?.();
        } catch (error: unknown) {
            toastStore.add('error', (error as Error).message ?? 'Failed to delete expense');
        } finally {
            deleting = false;
            deleteTarget = null;
        }
    }
</script>

<div class="space-y-4">
    <DataTable columns={columns} rows={expenses as Record<string, unknown>[]}>
        {#snippet cell({ row, key })}
            {@const expense = row as unknown as ExpenseDto}
            {#if key === 'description'}
                <span class="text-sm text-[var(--color-text-muted)]">{expense.description ?? '—'}</span>
            {:else if key === 'amount'}
                <span class="font-['DM_Mono'] text-sm text-[var(--color-gold)]">{formatCurrency(expense.amount ?? 0)}</span>
            {:else if key === 'date'}
                <span class="text-sm text-[var(--color-text-muted)]">{new Date(expense.createdAt).toLocaleDateString()}</span>
            {:else if key === 'actions'}
                <div class="flex items-center gap-3">
                    <a href={`/dashboard/expenses/${expense.id}?showId=${showId}`} class="text-sm text-[var(--color-gold)] hover:underline">
                        View
                    </a>
                    {#if !readOnly}
                        <button class="text-sm text-[var(--color-text-muted)] hover:text-[var(--color-text)]" onclick={() => { editExpense = expense; editOpen = true; }}>
                            Edit
                        </button>
                        <button class="text-sm text-[var(--color-error)]" onclick={() => { deleteTarget = expense; confirmOpen = true; }}>
                            Delete
                        </button>
                    {/if}
                </div>
            {:else}
                {expense.name}
            {/if}
        {/snippet}
    </DataTable>

    <Modal bind:open={editOpen} title="Edit Expense" size="md" onclose={() => editExpense = null}>
        {#snippet children()}
            {#if editExpense}
                <ExpenseForm
                    expense={editExpense}
                    {showId}
                    {clerkUserId}
                    onSaved={() => { editExpense = null; editOpen = false; }}
                />
            {/if}
        {/snippet}
    </Modal>

    <ConfirmDialog
        bind:open={confirmOpen}
        title="Delete Expense"
        message={deleteTarget ? `Delete ${deleteTarget.name}? This cannot be undone.` : 'Delete expense?'}
        confirmLabel={deleting ? 'Deleting…' : 'Delete'}
        onconfirm={handleDelete}
        oncancel={() => deleteTarget = null}
    />
</div>
