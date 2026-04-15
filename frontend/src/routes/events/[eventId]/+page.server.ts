import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { backendJsonOrNull, fetchShowsByEvent } from '$lib/server/backend';
import { EventDtoSchema, type ShowDto } from '$lib/types/api/event.types';

export const load: PageServerLoad = async ({ locals, params }) => {
    const userId = locals.user?.id;
    const event = await backendJsonOrNull<unknown>(locals, 'api/event/get', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, eventId: params.eventId })
    });

    if (!event) {
        error(404, 'Event not found');
    }

    let shows: ShowDto[] = [];
    try {
        shows = await fetchShowsByEvent(locals, params.eventId, userId);
    } catch {
        shows = [];
    }

    return {
        event: EventDtoSchema.parse(event),
        shows
    };
};
