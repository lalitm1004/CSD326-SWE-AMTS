<script lang="ts">
    import type { ShowDto } from '$lib/types/api/event.types';
    import ShowCard from './ShowCard.svelte';
    import EmptyState from '$lib/components/ui/EmptyState.svelte';

    interface Props {
        shows: ShowDto[];
        eventId: string;
    }
    let { shows, eventId }: Props = $props();
</script>

<div class="flex flex-col gap-3">
    {#if shows.length === 0}
        <EmptyState title="No shows scheduled" body="This event has no shows yet." />
    {:else}
        {#each shows as show (show.id)}
            <ShowCard {show} {eventId} />
        {/each}
    {/if}
</div>
