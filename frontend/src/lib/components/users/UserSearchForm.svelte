<script lang="ts">
    import { goto } from '$app/navigation';
    import Input from '$lib/components/ui/Input.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import { getUserByEmail } from '$lib/services/user.service';
    import { toastStore } from '$lib/stores/toast.store';
    import type { UserDto } from '$lib/types/api/user.types';

    interface Props {
        onFound?: (user: UserDto) => void;
    }

    let { onFound }: Props = $props();

    let email = $state('');
    let loading = $state(false);

    async function handleSubmit(event: SubmitEvent) {
        event.preventDefault();
        if (!email.trim()) {
            toastStore.add('warning', 'Enter an email address to search.');
            return;
        }

        loading = true;
        try {
            const user = await getUserByEmail(email.trim());
            onFound?.(user);
            if (!onFound) {
                goto(`/dashboard/users/${user.id}`);
            }
        } catch (error: unknown) {
            toastStore.add('error', (error as Error).message ?? 'User not found');
        } finally {
            loading = false;
        }
    }
</script>

<form onsubmit={handleSubmit} class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
    <div class="flex items-end gap-3">
        <Input
            label="Find User By Email"
            placeholder="student@example.edu"
            bind:value={email}
            class="flex-1"
            required
        />
        <Button type="submit" variant="primary" {loading}>Search</Button>
    </div>
</form>
