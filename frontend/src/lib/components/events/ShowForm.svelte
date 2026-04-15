<script lang="ts">
    import Input from '$lib/components/ui/Input.svelte';
    import Textarea from '$lib/components/ui/Textarea.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import { toastStore } from '$lib/stores/toast.store';
    import { createShow, updateShowName, updateShowDescription, updateShowSeatPrices, updateShowSeatCounts, updateShowStartingAt, updateShowEndingAt } from '$lib/services/show.service';
    import { UserStore } from '$lib/stores/SupaStore';
    import type { ShowDto } from '$lib/types/api/event.types';
    import { goto } from '$app/navigation';
    import { invalidateAll } from '$app/navigation';

    interface Props {
        eventId: string;
        show?: ShowDto;
        onSaved?: () => void;
    }
    let { eventId, show, onSaved }: Props = $props();

    let name = $state('');
    let description = $state('');
    let thumbnailUrl = $state('');
    let startingAt = $state('');
    let endingAt = $state('');
    let ordinarySeatPrice = $state('');
    let balconySeatPrice = $state('');
    let ordinarySeatCount = $state('');
    let balconySeatCount = $state('');
    let loading = $state(false);

    $effect(() => {
        name = show?.name ?? '';
        description = show?.description ?? '';
        thumbnailUrl = show?.thumbnailUrl ?? '';
        startingAt = show?.startingAt?.slice(0, 16) ?? '';
        endingAt = show?.endingAt?.slice(0, 16) ?? '';
        ordinarySeatPrice = show?.ordinarySeatPrice?.toString() ?? '';
        balconySeatPrice = show?.balconySeatPrice?.toString() ?? '';
        ordinarySeatCount = show?.ordinarySeatCount?.toString() ?? '';
        balconySeatCount = show?.balconySeatCount?.toString() ?? '';
    });

    async function handleSubmit(e: SubmitEvent) {
        e.preventDefault();
        loading = true;
        try {
            if (show) {
                // Edit: fire changed patches
                const uid = $UserStore!.id;
                await Promise.all([
                    name !== show.name ? updateShowName(show.id, name, uid) : null,
                    description !== (show.description ?? '') ? updateShowDescription(show.id, description, uid) : null,
                    (ordinarySeatPrice || balconySeatPrice) ? updateShowSeatPrices(show.id, uid, Number(ordinarySeatPrice), Number(balconySeatPrice)) : null,
                    (ordinarySeatCount || balconySeatCount) ? updateShowSeatCounts(show.id, uid, Number(ordinarySeatCount), Number(balconySeatCount)) : null,
                    startingAt ? updateShowStartingAt(show.id, uid, startingAt) : null,
                    endingAt ? updateShowEndingAt(show.id, uid, endingAt) : null,
                ].filter(Boolean));
                toastStore.add('success', 'Show updated.');
                await invalidateAll();
                onSaved?.();
            } else {
                await createShow({
                    eventId,
                    creatorUserId: $UserStore!.id,
                    name,
                    description,
                    thumbnailUrl,
                    startingAt: startingAt || undefined,
                    endingAt: endingAt || undefined,
                    ordinarySeatPrice: ordinarySeatPrice ? Number(ordinarySeatPrice) : undefined,
                    balconySeatPrice: balconySeatPrice ? Number(balconySeatPrice) : undefined,
                    ordinarySeatCount: ordinarySeatCount ? Number(ordinarySeatCount) : undefined,
                    balconySeatCount: balconySeatCount ? Number(balconySeatCount) : undefined,
                });
                toastStore.add('success', 'Show created.');
                goto(`/dashboard/events/${eventId}`);
            }
        } catch (err: unknown) {
            toastStore.add('error', (err as Error).message ?? 'Failed to save show');
        } finally {
            loading = false;
        }
    }
</script>

<form onsubmit={handleSubmit} class="flex flex-col gap-5">
    <Input label="Show Name" required bind:value={name} placeholder="Opening Night" />
    <Textarea label="Description" bind:value={description} rows={2} />
    {#if !show}
        <Input label="Thumbnail URL" bind:value={thumbnailUrl} placeholder="https://..." />
    {/if}
    <div class="grid grid-cols-2 gap-4">
        <Input label="Starts At" type="datetime-local" bind:value={startingAt} />
        <Input label="Ends At" type="datetime-local" bind:value={endingAt} />
    </div>
    <div class="grid grid-cols-2 gap-4">
        <Input label="Ordinary Price (₹)" type="number" bind:value={ordinarySeatPrice} placeholder="500" />
        <Input label="Balcony Price (₹)" type="number" bind:value={balconySeatPrice} placeholder="800" />
    </div>
    <div class="grid grid-cols-2 gap-4">
        <Input label="Ordinary Seat Count" type="number" bind:value={ordinarySeatCount} placeholder="600" />
        <Input label="Balcony Seat Count" type="number" bind:value={balconySeatCount} placeholder="200" />
    </div>
    <div class="flex justify-end gap-3 pt-2">
        <Button type="submit" {loading}>
            {show ? 'Save Changes' : 'Create Show'}
        </Button>
    </div>
</form>
