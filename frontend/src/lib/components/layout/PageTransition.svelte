<script lang="ts">
    import type { Snippet } from 'svelte';
    import { fade } from 'svelte/transition';
    import { page } from '$app/stores';

    interface Props {
        children: Snippet;
    }
    let { children }: Props = $props();
</script>

{#key $page.url.pathname}
    <div transition:fade={{ duration: 150 }}>
        {@render children()}
    </div>
{/key}
