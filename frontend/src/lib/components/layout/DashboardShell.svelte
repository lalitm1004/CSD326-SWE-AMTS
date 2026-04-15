<script lang="ts">
    import type { Snippet } from 'svelte';
    import { setContext } from 'svelte';
    import { rolesStore } from '$lib/stores/roles.store';
    import { UserStore } from '$lib/stores/SupaStore';
    import Sidebar from './Sidebar.svelte';
    import Topbar from './Topbar.svelte';

    interface Props {
        children: Snippet;
    }
    let { children }: Props = $props();

    // Set context so nested components can access roles without prop drilling
    setContext('dashboard', {
        get roles() { return $rolesStore; },
        get user() { return $UserStore; }
    });
</script>

<div class="flex h-dvh bg-[var(--color-canvas)] overflow-hidden">
    <Sidebar />
    <div class="flex flex-col flex-1 min-w-0 overflow-hidden">
        <Topbar />
        <main class="flex-1 overflow-y-auto">
            <div class="max-w-6xl mx-auto px-8 py-8">
                {@render children()}
            </div>
        </main>
    </div>
</div>
