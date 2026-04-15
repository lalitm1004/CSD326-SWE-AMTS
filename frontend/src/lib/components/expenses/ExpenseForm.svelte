<script lang="ts">
    import { goto, invalidateAll } from '$app/navigation';
    import Button from '$lib/components/ui/Button.svelte';
    import Input from '$lib/components/ui/Input.svelte';
    import Textarea from '$lib/components/ui/Textarea.svelte';
    import { createExpense, updateExpenseAmount, updateExpenseDescription, updateExpenseName } from '$lib/services/expense.service';
    import { toastStore } from '$lib/stores/toast.store';
    import type { ExpenseDto } from '$lib/types/api/expense.types';

    interface Props {
        showId: string;
        clerkUserId: string;
        expense?: ExpenseDto;
        readOnly?: boolean;
        onSaved?: () => void;
    }

    let { showId, clerkUserId, expense, readOnly = false, onSaved }: Props = $props();

    let name = $state('');
    let description = $state('');
    let amount = $state('');
    let loading = $state(false);

    $effect(() => {
        name = expense?.name ?? '';
        description = expense?.description ?? '';
        amount = expense?.amount?.toString() ?? '';
    });

    async function handleSubmit(event: SubmitEvent) {
        event.preventDefault();
        if (readOnly) return;

        loading = true;
        try {
            if (expense) {
                const updates: Promise<void>[] = [];
                if (name !== expense.name) updates.push(updateExpenseName(expense.id, clerkUserId, name));
                if (description !== (expense.description ?? '')) updates.push(updateExpenseDescription(expense.id, clerkUserId, description));
                if (Number(amount) !== expense.amount) updates.push(updateExpenseAmount(expense.id, clerkUserId, Number(amount)));
                await Promise.all(updates);
                toastStore.add('success', 'Expense updated.');
            } else {
                await createExpense({
                    clerkUserId,
                    showId,
                    name,
                    description: description.trim() || undefined,
                    amount: Number(amount)
                });
                toastStore.add('success', 'Expense created.');
            }

            await invalidateAll();
            onSaved?.();
        } catch (error: unknown) {
            toastStore.add('error', (error as Error).message ?? 'Failed to save expense');
        } finally {
            loading = false;
        }
    }
</script>

<form onsubmit={handleSubmit} class="space-y-5">
    <Input label="Expense Name" bind:value={name} required disabled={readOnly} />
    <Textarea label="Description" bind:value={description} rows={3} disabled={readOnly} />
    <Input label="Amount (₹)" bind:value={amount} type="number" required disabled={readOnly} />

    {#if !readOnly}
        <div class="flex justify-end">
            <Button type="submit" variant="primary" {loading}>
                {expense ? 'Save Expense' : 'Create Expense'}
            </Button>
        </div>
    {/if}
</form>
