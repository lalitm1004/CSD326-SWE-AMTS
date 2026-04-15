<script lang="ts">
    import type { Snippet } from 'svelte';
    import { rolesStore, hasRole } from '$lib/stores/roles.store';
    import type { RoleEnumT } from '$lib/types/role.type';

    interface Props {
        roles: RoleEnumT[];
        fallback?: Snippet;
        children: Snippet;
    }

    let { roles, fallback, children }: Props = $props();
    const allowed = $derived(hasRole($rolesStore, ...roles));
</script>

{#if allowed}
    {@render children()}
{:else if fallback}
    {@render fallback()}
{/if}
