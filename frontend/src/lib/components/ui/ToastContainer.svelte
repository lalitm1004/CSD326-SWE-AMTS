<script lang="ts">
    import { toastStore } from '$lib/stores/toast.store';
    import Toast from './Toast.svelte';
</script>

<div class="fixed bottom-6 right-6 z-[100] flex flex-col gap-2 items-end pointer-events-none">
    {#each $toastStore as toast (toast.id)}
        <div class="pointer-events-auto">
            <Toast {toast} />
        </div>
    {/each}
</div>
