<script lang="ts">
    import type { PageData } from './$types';
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import SeatGrid from '$lib/components/seats/SeatGrid.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import { SessionStore } from '$lib/stores/SupaStore';

    let { data }: { data: PageData } = $props();

    const isLoggedIn = $derived(!!$SessionStore);

    const formatDate = (s: string | null | undefined) => {
        if (!s) return null;
        return new Date(s).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' });
    };

    const soldCount = $derived(data.seats.filter(s => s.isAvailable === false).length);
    const availableCount = $derived(data.seats.filter(s => s.isAvailable !== false && s.designation === 'ORDINARY').length);
</script>

<svelte:head>
    <title>{data.show.name} — AMTS</title>
</svelte:head>

<PublicNav />

<div class="max-w-5xl mx-auto px-6 py-10">
    <PageHeader
        title={data.show.name}
        subtitle={data.show.description ?? undefined}
        breadcrumbs={[
            { label: 'Events', href: '/events' },
            { label: 'Event', href: `/events/${data.eventId}` },
            { label: data.show.name }
        ]}
    />

    <!-- Show info grid -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-10">
        {#if data.show.startingAt}
            <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-4">
                <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)] mb-1">Starts</p>
                <p class="text-sm text-[var(--color-text)]">{formatDate(data.show.startingAt)}</p>
            </div>
        {/if}
        <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-4">
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)] mb-1">Ordinary</p>
            <p class="font-['Cormorant_Garamond'] text-xl text-[var(--color-gold)]">
                {data.show.ordinarySeatPrice != null ? `₹${data.show.ordinarySeatPrice}` : 'TBA'}
            </p>
        </div>
        <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-4">
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)] mb-1">Balcony</p>
            <p class="font-['Cormorant_Garamond'] text-xl text-[var(--color-gold)]">
                {data.show.balconySeatPrice != null ? `₹${data.show.balconySeatPrice}` : 'TBA'}
            </p>
        </div>
        <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-4">
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)] mb-1">Available</p>
            <p class="font-['Cormorant_Garamond'] text-xl text-[var(--color-text)]">{availableCount}</p>
        </div>
    </div>

    <!-- Book CTA -->
    {#if isLoggedIn}
        <div class="mb-8 flex justify-end">
            <Button variant="primary" onclick={() => window.location.href = `/events/${data.eventId}/shows/${data.show.id}/book`}>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 12V22H4V12"/><path d="M22 7H2v5h20V7z"/><path d="M12 22V7"/><path d="M12 7H7.5a2.5 2.5 0 010-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 000-5C13 2 12 7 12 7z"/>
                </svg>
                Book Tickets
            </Button>
        </div>
    {:else}
        <div class="mb-8 flex items-center justify-end gap-3">
            <p class="text-sm text-[var(--color-text-muted)]">Sign in to book tickets</p>
            <Button variant="secondary" onclick={() => window.location.href = '/auth'}>Sign In</Button>
        </div>
    {/if}

    <!-- Seat map -->
    <div class="flex items-center gap-4 mb-6">
        <div class="flex-1 h-px bg-[var(--color-border)]"></div>
        <span class="text-[10px] text-[var(--color-gold-dim)] uppercase tracking-widest">Seat Map</span>
        <div class="flex-1 h-px bg-[var(--color-border)]"></div>
    </div>

    <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-6">
        <SeatGrid seats={data.seats} mode="view" />
    </div>
</div>
