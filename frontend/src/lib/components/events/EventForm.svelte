<script lang="ts">
    import Input from '$lib/components/ui/Input.svelte';
    import Textarea from '$lib/components/ui/Textarea.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import { toastStore } from '$lib/stores/toast.store';
    import { createEvent, updateEventName, updateEventDescription } from '$lib/services/event.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import type { EventDto } from '$lib/types/api/event.types';
    import { goto } from '$app/navigation';

    interface Props {
        event?: EventDto;
        onSaved?: (event: EventDto) => void;
    }
    let { event, onSaved }: Props = $props();

    let name = $state('');
    let description = $state('');
    let thumbnailUrl = $state('');
    let loading = $state(false);

    $effect(() => {
        name = event?.name ?? '';
        description = event?.description ?? '';
        thumbnailUrl = event?.thumbnailUrl ?? '';
    });

    async function handleSubmit(e: SubmitEvent) {
        e.preventDefault();
        loading = true;
        try {
            if (event) {
                // Edit mode
                await Promise.all([
                    name !== event.name ? updateEventName(event.id, name, $UserStore!.id) : Promise.resolve(),
                    description !== (event.description ?? '') ? updateEventDescription(event.id, description, $UserStore!.id) : Promise.resolve(),
                ]);
                toastStore.add('success', 'Event updated.');
                onSaved?.(event);
            } else {
                // Create mode
                await createEvent({ name, description, thumbnailUrl, creatorUserId: $UserStore!.id });
                toastStore.add('success', 'Event created.');
                goto('/dashboard/events');
            }
        } catch (err: unknown) {
            toastStore.add('error', (err as Error).message ?? 'Failed to save event');
        } finally {
            loading = false;
        }
    }
</script>

<form onsubmit={handleSubmit} class="flex flex-col gap-5">
    <Input label="Event Name" required bind:value={name} placeholder="Annual Cultural Fest" />
    <Textarea label="Description" bind:value={description} placeholder="Describe the event…" rows={3} />
    {#if !event}
        <Input label="Thumbnail URL" bind:value={thumbnailUrl} placeholder="https://..." />
    {/if}
    <div class="flex justify-end gap-3 pt-2">
        <Button type="submit" {loading}>
            {event ? 'Save Changes' : 'Create Event'}
        </Button>
    </div>
</form>
