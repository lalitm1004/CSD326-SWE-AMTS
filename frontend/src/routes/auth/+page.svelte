<script lang="ts">
    import AuthForm from '$lib/components/auth/AuthForm.svelte';
</script>

<svelte:head>
    <title>Sign In — AMTS</title>
</svelte:head>

<div class="min-h-dvh bg-[var(--color-canvas)] flex items-center justify-center px-6">
    <!-- Background ornament -->
    <div class="fixed inset-0 pointer-events-none overflow-hidden">
        <div class="absolute top-1/4 left-1/2 -translate-x-1/2 w-96 h-96 rounded-full bg-[var(--color-gold-dim)] opacity-[0.03] blur-3xl"></div>
    </div>

    <div class="relative w-full max-w-sm">
        <!-- Header -->
        <div class="text-center mb-10">
            <a href="/" class="font-['Cormorant_Garamond'] text-3xl font-semibold text-[var(--color-text)] tracking-wide">
                <span class="text-[var(--color-gold)] mr-2">◆</span>AMTS
            </a>
            <p class="text-sm text-[var(--color-text-muted)] mt-2">Auditorium Management & Ticketing</p>
            <div class="flex items-center justify-center gap-3 mt-4">
                <div class="w-12 h-px bg-[var(--color-border-gold)] opacity-40"></div>
                <span class="text-[var(--color-gold-dim)] text-[10px]">◆</span>
                <div class="w-12 h-px bg-[var(--color-border-gold)] opacity-40"></div>
            </div>
        </div>

        <div class="bg-[var(--color-surface)] border border-[var(--color-border)] p-8 corner-ornament">
            <AuthForm />
        </div>
    </div>
</div>
