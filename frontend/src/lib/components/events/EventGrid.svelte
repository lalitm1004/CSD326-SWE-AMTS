<script lang="ts">
    import type { EventDto } from '$lib/types/api/event.types';
    import EventCard from './EventCard.svelte';

    interface Props {
        events: EventDto[];
    }
    let { events }: Props = $props();
</script>

<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
    {#each events as event (event.id)}
        <EventCard {event} />
    {/each}
</div>
