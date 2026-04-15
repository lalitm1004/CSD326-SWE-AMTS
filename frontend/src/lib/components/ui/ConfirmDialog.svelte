<script lang="ts">
    import Modal from './Modal.svelte';
    import Button from './Button.svelte';

    interface Props {
        open: boolean;
        title?: string;
        message: string;
        confirmLabel?: string;
        onconfirm: () => void;
        oncancel?: () => void;
    }

    let {
        open = $bindable(),
        title = 'Confirm Action',
        message,
        confirmLabel = 'Confirm',
        onconfirm,
        oncancel
    }: Props = $props();

    function handleConfirm() {
        open = false;
        onconfirm();
    }

    function handleCancel() {
        open = false;
        oncancel?.();
    }
</script>

<Modal bind:open {title} size="sm" onclose={handleCancel}>
    {#snippet children()}
        <p class="text-sm text-[var(--color-text-muted)]">{message}</p>
    {/snippet}
    {#snippet footer()}
        <Button variant="ghost" onclick={handleCancel}>Cancel</Button>
        <Button variant="danger" onclick={handleConfirm}>{confirmLabel}</Button>
    {/snippet}
</Modal>
