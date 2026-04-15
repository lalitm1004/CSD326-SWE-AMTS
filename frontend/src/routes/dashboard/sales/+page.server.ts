import type { PageServerLoad } from './$types';
import { fetchAllEvents, fetchShowSeats, fetchShowsByEvent } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id;
    const selectedEventId = url.searchParams.get('eventId') ?? '';
    const selectedShowId = url.searchParams.get('showId') ?? '';

    const events = await fetchAllEvents(locals, actorUserId);
    const shows = selectedEventId ? await fetchShowsByEvent(locals, selectedEventId, actorUserId) : [];
    const seats = selectedShowId ? await fetchShowSeats(locals, selectedShowId, actorUserId) : [];

    return {
        events,
        shows,
        seats,
        selectedEventId,
        selectedShowId
    };
};
