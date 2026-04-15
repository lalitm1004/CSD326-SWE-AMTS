<script lang="ts">
    import type { PageData } from './$types';
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import ShowList from '$lib/components/events/ShowList.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';

    let { data }: { data: PageData } = $props();

    const formatDate = (s: string | null | undefined) => {
        if (!s) return null;
        return new Date(s).toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' });
    };
</script>

<svelte:head>
    <title>{data.event.name} — AMTS</title>
</svelte:head>

<PublicNav />

<div class="max-w-4xl mx-auto px-6 py-10">
    <!-- Thumbnail -->
    {#if data.event.thumbnailUrl}
        <div class="w-full aspect-[21/6] overflow-hidden bg-[var(--color-raised)] mb-8">
            <img src={data.event.thumbnailUrl} alt={data.event.name} class="w-full h-full object-cover opacity-80" />
        </div>
    {/if}

    <PageHeader
        title={data.event.name}
        subtitle={data.event.description ?? undefined}
        breadcrumbs={[
            { label: 'Events', href: '/events' },
            { label: data.event.name }
        ]}
    />

    {#if data.event.startingAt || data.event.endingAt}
        <div class="flex items-center gap-2 mb-8 text-sm text-[var(--color-text-muted)]">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-[var(--color-gold)] shrink-0">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            {#if data.event.startingAt && data.event.endingAt}
                {formatDate(data.event.startingAt)} — {formatDate(data.event.endingAt)}
            {:else}
                {formatDate(data.event.startingAt ?? data.event.endingAt)}
            {/if}
        </div>
    {/if}

    <div class="flex items-center gap-4 mb-6">
        <div class="flex-1 h-px bg-[var(--color-border)]"></div>
        <span class="text-[10px] text-[var(--color-gold-dim)] uppercase tracking-widest">Shows</span>
        <div class="flex-1 h-px bg-[var(--color-border)]"></div>
    </div>

    <ShowList shows={data.shows} eventId={data.event.id} />
</div>
