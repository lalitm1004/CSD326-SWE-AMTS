<script lang="ts">
    import type { Snippet } from 'svelte';
    import DashboardShell from '$lib/components/layout/DashboardShell.svelte';

    interface Props {
        children: Snippet;
        data: { roles: string[] };
    }

    let { children, data }: Props = $props();
</script>

<DashboardShell>
    {@render children()}
</DashboardShell>
