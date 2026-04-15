<script lang="ts">
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import Divider from '$lib/components/ui/Divider.svelte';
    import { purchaseCoupon } from '$lib/services/ticket.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import { toastStore } from '$lib/stores/toast.store';
    import Select from '$lib/components/ui/Select.svelte';
    import { getAllEvents, getShowsByEvent } from '$lib/services/event.service';
    import type { EventDto, ShowDto } from '$lib/types/api/event.types';
    import { onMount } from 'svelte';

    let events = $state<EventDto[]>([]);
    let shows = $state<ShowDto[]>([]);
    let selectedEventId = $state('');
    let selectedShowId = $state('');
    let loading = $state(false);
    let couponResult = $state<string | null>(null);

    onMount(async () => {
        try { events = await getAllEvents(); } catch {}
    });

    $effect(() => {
        if (selectedEventId) {
            shows = [];
            selectedShowId = '';
            getShowsByEvent(selectedEventId).then(s => shows = s).catch(() => {});
        }
    });

    async function handlePurchase() {
        if (!selectedShowId) {
            toastStore.add('warning', 'Please select a show');
            return;
        }
        loading = true;
        try {
            couponResult = await purchaseCoupon($UserStore!.id, selectedShowId);
            toastStore.add('success', 'Coupon purchased!');
        } catch (e: unknown) {
            toastStore.add('error', (e as Error).message ?? 'Failed to purchase coupon');
        } finally {
            loading = false;
        }
    }
</script>

<svelte:head>
    <title>Coupons — AMTS</title>
</svelte:head>

<PublicNav />

<div class="max-w-xl mx-auto px-6 py-10">
    <PageHeader title="Discount Coupon" subtitle="Purchase a ₹1,000 coupon for 10% off your ticket order" />
    <Divider />

    <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-6 mt-6 corner-ornament">
        <div class="flex flex-col gap-5">
            <Select
                label="Event"
                options={events.map(e => ({ value: e.id, label: e.name }))}
                bind:value={selectedEventId}
                placeholder="Select event…"
            />
            <Select
                label="Show"
                options={shows.map(s => ({ value: s.id, label: s.name }))}
                bind:value={selectedShowId}
                placeholder={selectedEventId ? 'Select show…' : 'Select event first'}
                disabled={!selectedEventId}
            />

            <Divider />

            <div class="flex items-center justify-between">
                <div>
                    <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)]">Coupon Value</p>
                    <p class="font-['Cormorant_Garamond'] text-3xl text-[var(--color-gold)]">₹1,000</p>
                    <p class="text-xs text-[var(--color-text-muted)] mt-1">Gives 10% off all seats in one booking</p>
                </div>
                <Button variant="primary" {loading} onclick={handlePurchase}>
                    Purchase Coupon
                </Button>
            </div>
        </div>
    </div>

    {#if couponResult}
        <div class="mt-6 bg-[var(--color-surface)] border border-[var(--color-success)] p-5 corner-ornament">
            <p class="text-[10px] uppercase tracking-widest text-[var(--color-success)] mb-2">Your Coupon Code</p>
            <p class="font-['DM_Mono'] text-2xl text-[var(--color-text)] tracking-wider">{couponResult}</p>
            <p class="text-xs text-[var(--color-text-muted)] mt-2">Save this code — enter it at checkout for 10% off.</p>
        </div>
    {/if}
</div>
