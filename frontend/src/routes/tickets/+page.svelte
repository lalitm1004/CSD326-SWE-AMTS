<script lang="ts">
    import type { PageData } from './$types';
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
    import PageHeader from '$lib/components/ui/PageHeader.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';
    import Badge from '$lib/components/ui/Badge.svelte';
    import Button from '$lib/components/ui/Button.svelte';

    let { data }: { data: PageData } = $props();
    let bookingId = $state('');
    let bookings = $derived(data.bookings);

    const formatCurrency = (amount: number | null | undefined) =>
        amount == null
            ? 'Pending'
            : new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(amount);
</script>

<svelte:head>
    <title>My Tickets — AMTS</title>
</svelte:head>

<PublicNav />

<div class="max-w-3xl mx-auto px-6 py-10">
    <PageHeader title="My Tickets" subtitle="View and manage your bookings" />

    {#if bookings.length > 0}
        <div class="mb-8">
            <div class="flex items-center gap-4 mb-5">
                <div class="flex-1 h-px bg-[var(--color-border)]"></div>
                <span class="text-[10px] text-[var(--color-gold-dim)] uppercase tracking-widest">Your Bookings</span>
                <div class="flex-1 h-px bg-[var(--color-border)]"></div>
            </div>

            <div class="grid gap-4">
                {#each bookings as booking (booking.id)}
                    <div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
                        <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                            <div class="space-y-3">
                                <div class="flex flex-wrap items-center gap-2">
                                    <p class="font-['Cormorant_Garamond'] text-2xl text-[var(--color-text)]">
                                        Booking {booking.code}
                                    </p>
                                    <Badge variant={booking.type === 'OFFLINE' ? 'info' : 'success'}>
                                        {booking.type}
                                    </Badge>
                                    {#if booking.refundedTicketCount > 0}
                                        <Badge variant="warning">
                                            {booking.refundedTicketCount} refunded
                                        </Badge>
                                    {/if}
                                </div>

                                <div class="grid gap-3 text-sm text-[var(--color-text-muted)] md:grid-cols-3">
                                    <div>
                                        <p class="text-[10px] uppercase tracking-widest mb-1">Tickets</p>
                                        <p class="font-['DM_Mono'] text-[var(--color-text)]">
                                            {booking.ticketCount}
                                        </p>
                                    </div>
                                    <div>
                                        <p class="text-[10px] uppercase tracking-widest mb-1">Amount</p>
                                        <p class="font-['DM_Mono'] text-[var(--color-text)]">
                                            {formatCurrency(booking.amount)}
                                        </p>
                                    </div>
                                    <div>
                                        <p class="text-[10px] uppercase tracking-widest mb-1">Booked On</p>
                                        <p class="font-['DM_Mono'] text-[var(--color-text)]">
                                            {new Date(booking.createdAt).toLocaleString()}
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <div class="md:pl-6">
                                <a
                                    href={`/tickets/${booking.id}`}
                                    class="inline-flex items-center justify-center px-4 py-2 text-sm bg-[var(--color-gold)] text-[var(--color-text-on-gold)] hover:bg-[var(--color-gold-light)] transition-colors"
                                >
                                    View Tickets
                                </a>
                            </div>
                        </div>
                    </div>
                {/each}
            </div>
        </div>
    {/if}

    <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-6 corner-ornament">
        <p class="text-[10px] uppercase tracking-widest text-[var(--color-text-muted)] mb-2">Look up a booking</p>
        <div class="flex gap-3">
            <input
                type="text"
                placeholder="Booking ID"
                bind:value={bookingId}
                class="flex-1 px-3 py-2 text-sm bg-[var(--color-raised)] text-[var(--color-text)] border border-[var(--color-border)] focus:outline-none focus:border-[var(--color-gold)] font-['DM_Mono'] placeholder:text-[var(--color-text-muted)]"
            />
            <Button
                variant="primary"
                onclick={() => bookingId.trim() && (window.location.href = `/tickets/${bookingId.trim()}`)}
            >
                View
            </Button>
        </div>
    </div>

    {#if bookings.length === 0}
        <div class="mt-8">
            <EmptyState
                title="No recent bookings"
                body="Your confirmed bookings will appear here. Use the lookup above to find a specific booking."
            />
        </div>
    {/if}
</div>
