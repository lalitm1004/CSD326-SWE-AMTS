import type { PageServerLoad } from './$types';
import { fetchAllEvents, fetchShowsByEvent, fetchUser } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const selectedEventId = url.searchParams.get('eventId') ?? '';
    const selectedShowId = url.searchParams.get('showId') ?? '';

    const [events, actor] = await Promise.all([
        fetchAllEvents(locals, actorUserId),
        actorUserId ? fetchUser(locals, actorUserId) : Promise.resolve(null)
    ]);

    return {
        actorUserId,
        canEdit: (actor?.roles ?? []).some((role) => ['ROOT', 'FINANCIAL_CLERK'].includes(role)),
        events,
        shows: selectedEventId ? await fetchShowsByEvent(locals, selectedEventId, actorUserId) : [],
        selectedEventId,
        selectedShowId
    };
};
