<script lang="ts">
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import EventGrid from '$lib/components/events/EventGrid.svelte';
    import { getAllEvents } from '$lib/services/event.service';
    import type { EventDto } from '$lib/types/api/event.types';
    import { onMount } from 'svelte';
    import Spinner from '$lib/components/ui/Spinner.svelte';

    let events = $state<EventDto[]>([]);
    let loading = $state(true);
    let error = $state<string | null>(null);

    onMount(async () => {
        try {
            events = await getAllEvents();
        } catch {
            error = 'Could not load events.';
        } finally {
            loading = false;
        }
    });

    const upcoming = $derived(
        events.filter(e => {
            if (!e.endingAt) return true;
            return new Date(e.endingAt) >= new Date();
        }).slice(0, 6)
    );
</script>

<svelte:head>
    <title>AMTS — Auditorium Management &amp; Ticketing</title>
</svelte:head>

<PublicNav />

<!-- Hero -->
<section class="relative min-h-[60vh] flex items-center overflow-hidden">
    <div class="absolute inset-0 pointer-events-none">
        <div class="absolute top-0 left-0 w-full h-full bg-[radial-gradient(ellipse_at_30%_20%,rgba(201,149,76,0.06)_0%,transparent_60%)]"></div>
        <div class="absolute bottom-0 right-0 w-1/2 h-1/2 bg-[radial-gradient(ellipse_at_70%_80%,rgba(201,149,76,0.04)_0%,transparent_60%)]"></div>
        <div class="absolute top-16 right-16 w-px h-32 bg-[var(--color-border-gold)] opacity-20 rotate-12"></div>
        <div class="absolute top-24 right-24 w-px h-20 bg-[var(--color-border-gold)] opacity-10 rotate-12"></div>
    </div>

    <div class="max-w-7xl mx-auto px-6 w-full">
        <div class="max-w-2xl">
            <p class="text-[10px] text-[var(--color-gold)] uppercase tracking-[0.3em] mb-4">
                ◆ &nbsp; Welcome to the Stage
            </p>
            <h1 class="font-['Cormorant_Garamond'] text-6xl md:text-7xl font-semibold text-[var(--color-text)] leading-[0.9] tracking-tight mb-6">
                Where Every<br/>
                <em class="text-[var(--color-gold)] not-italic">Performance</em><br/>
                Begins
            </h1>
            <p class="text-[var(--color-text-muted)] text-base max-w-md leading-relaxed mb-8">
                Book your seat for upcoming shows, cultural events, and performances at our college auditorium.
            </p>
            <div class="flex items-center gap-4">
                <a href="/events" class="inline-flex items-center gap-2 px-6 py-3 bg-[var(--color-gold)] text-[var(--color-text-on-gold)] text-sm font-medium hover:bg-[var(--color-gold-light)] transition-colors">
                    Browse Events
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M5 12h14M12 5l7 7-7 7"/>
                    </svg>
                </a>
                <a href="/auth" class="text-sm text-[var(--color-text-muted)] hover:text-[var(--color-text)] transition-colors">
                    Sign in to manage bookings →
                </a>
            </div>
        </div>
    </div>
</section>

<!-- Divider -->
<div class="max-w-7xl mx-auto px-6">
    <div class="flex items-center gap-4">
        <div class="flex-1 h-px bg-[var(--color-border)]"></div>
        <span class="text-[10px] text-[var(--color-gold-dim)] uppercase tracking-[0.3em]">Upcoming Events</span>
        <div class="flex-1 h-px bg-[var(--color-border)]"></div>
    </div>
</div>

<!-- Events section -->
<section class="max-w-7xl mx-auto px-6 py-12">
    {#if loading}
        <div class="flex justify-center py-16">
            <Spinner size="lg" />
        </div>
    {:else if error}
        <p class="text-center text-[var(--color-text-muted)] py-16">{error}</p>
    {:else if upcoming.length === 0}
        <div class="text-center py-16">
            <p class="font-['Cormorant_Garamond'] text-2xl text-[var(--color-text-muted)]">No upcoming events</p>
            <p class="text-sm text-[var(--color-text-muted)] mt-2">Check back soon for new shows.</p>
        </div>
    {:else}
        <EventGrid events={upcoming} />
        {#if events.length > 6}
            <div class="text-center mt-10">
                <a href="/events" class="text-sm text-[var(--color-gold)] hover:text-[var(--color-gold-light)] transition-colors">
                    View all {events.length} events →
                </a>
            </div>
        {/if}
    {/if}
</section>
