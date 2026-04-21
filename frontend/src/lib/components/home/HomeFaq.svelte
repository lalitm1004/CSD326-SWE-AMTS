<script lang="ts">
    import type { FaqCategory } from '$lib/content/homeFaq';

    interface Props {
        categories: FaqCategory[];
    }

    let { categories }: Props = $props();
</script>

<section class="max-w-7xl mx-auto px-6 py-16 md:py-20">
    <div class="grid gap-10 lg:grid-cols-[minmax(0,18rem)_minmax(0,1fr)] lg:gap-12">
        <div class="lg:sticky lg:top-24 self-start">
            <p class="text-[10px] text-[var(--color-gold)] uppercase tracking-[0.3em] mb-4">
                ◆ &nbsp; Frequently Asked Questions
            </p>
            <h2 class="font-['Cormorant_Garamond'] text-4xl md:text-5xl font-semibold text-[var(--color-text)] leading-tight">
                Full-System Guidance for AMTS
            </h2>
            <p class="text-sm md:text-base text-[var(--color-text-muted)] leading-relaxed mt-4 max-w-md">
                This FAQ summarizes the wider AMTS product using the SRS and the current application structure. It covers public browsing, booking, operations, reporting, and system policies.
            </p>
        </div>

        <div class="space-y-6">
            {#each categories as category}
                <section class="corner-ornament bg-[var(--color-surface)] border border-[var(--color-border)] p-5 md:p-6">
                    <div class="mb-5">
                        <p class="text-[10px] uppercase tracking-[0.28em] text-[var(--color-gold-dim)] mb-2">
                            {category.title}
                        </p>
                        <p class="text-sm text-[var(--color-text-muted)] leading-relaxed">
                            {category.description}
                        </p>
                    </div>

                    <div class="space-y-3">
                        {#each category.items as item}
                            <details class="group border border-[var(--color-border)] bg-[var(--color-canvas)]/45 open:border-[var(--color-border-gold)] transition-colors">
                                <summary class="list-none cursor-pointer px-4 py-4 md:px-5 md:py-4">
                                    <div class="flex items-start justify-between gap-4">
                                        <h3 class="font-medium text-sm md:text-base text-[var(--color-text)] leading-relaxed">
                                            {item.question}
                                        </h3>
                                        <span class="mt-0.5 shrink-0 text-[var(--color-gold)] transition-transform duration-200 group-open:rotate-45">
                                            +
                                        </span>
                                    </div>
                                </summary>

                                <div class="px-4 pb-4 md:px-5 md:pb-5 border-t border-[var(--color-border)]">
                                    <div class="space-y-3 pt-4">
                                        {#each item.answer as paragraph}
                                            <p class="text-sm text-[var(--color-text-muted)] leading-7">
                                                {paragraph}
                                            </p>
                                        {/each}
                                    </div>

                                    {#if item.srsRefs?.length}
                                        <div class="flex flex-wrap gap-2 mt-4">
                                            {#each item.srsRefs as ref}
                                                <span class="inline-flex items-center px-2.5 py-1 text-[10px] uppercase tracking-[0.2em] border border-[var(--color-border-gold)] text-[var(--color-gold)]">
                                                    {ref}
                                                </span>
                                            {/each}
                                        </div>
                                    {/if}
                                </div>
                            </details>
                        {/each}
                    </div>
                </section>
            {/each}
        </div>
    </div>
</section>
