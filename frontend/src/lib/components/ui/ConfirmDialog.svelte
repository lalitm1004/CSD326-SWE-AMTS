<script lang="ts">
    import Modal from './Modal.svelte';
    import Button from './Button.svelte';

    interface Props {
        open: boolean;
        title?: string;
        message: string;
        confirmLabel?: string;
        confirmLoading?: boolean;
        onconfirm: () => void | Promise<void>;
        oncancel?: () => void;
    }

    let {
        open = $bindable(),
        title = 'Confirm Action',
        message,
        confirmLabel = 'Confirm',
        confirmLoading = false,
        onconfirm,
        oncancel
    }: Props = $props();

    async function handleConfirm() {
        try {
            await onconfirm();
            if (!confirmLoading) {
                open = false;
            }
        } catch {
            // Keep the dialog open when confirmation fails.
        }
    }

    function handleCancel() {
        if (confirmLoading) return;
        open = false;
        oncancel?.();
    }
</script>

<Modal bind:open {title} size="sm" onclose={handleCancel}>
    {#snippet children()}
        <p class="text-sm text-[var(--color-text-muted)]">{message}</p>
    {/snippet}
    {#snippet footer()}
        <Button variant="ghost" onclick={handleCancel} disabled={confirmLoading}>Cancel</Button>
        <Button variant="danger" onclick={handleConfirm} loading={confirmLoading}>{confirmLabel}</Button>
    {/snippet}
</Modal>
