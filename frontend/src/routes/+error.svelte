<script lang="ts">
    import PublicNav from '$lib/components/layout/PublicNav.svelte';
</script>

<svelte:head>
    <title>Page Not Found — AMTS</title>
</svelte:head>

<PublicNav />

<section class="min-h-[70vh] flex items-center">
    <div class="max-w-3xl mx-auto px-6 py-16 text-center">
        <p class="text-[10px] text-[var(--color-gold)] uppercase tracking-[0.3em] mb-4">
            ◆ &nbsp; 404
        </p>
        <h1 class="font-['Cormorant_Garamond'] text-5xl md:text-6xl font-semibold text-[var(--color-text)] leading-tight">
            Page Not Found
        </h1>
        <p class="text-[var(--color-text-muted)] text-base leading-relaxed max-w-xl mx-auto mt-5">
            The page you tried to open is unavailable.
        </p>

        <div class="flex flex-col sm:flex-row items-center justify-center gap-4 mt-8">
            <a
                href="/"
                class="inline-flex items-center gap-2 px-6 py-3 bg-[var(--color-gold)] text-[var(--color-text-on-gold)] text-sm font-medium hover:bg-[var(--color-gold-light)] transition-colors"
            >
                Go Home
            </a>
            <a
                href="/events"
                class="text-sm text-[var(--color-text-muted)] hover:text-[var(--color-text)] transition-colors"
            >
                Browse Events
            </a>
        </div>
    </div>
</section>
