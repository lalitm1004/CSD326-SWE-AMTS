import type { PageServerLoad } from './$types';
import { fetchAllEvents, fetchBalanceSheet, fetchShow, fetchShowsByEvent } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const selectedEventId = url.searchParams.get('eventId') ?? '';
    const selectedShowId = url.searchParams.get('showId') ?? '';

    const events = await fetchAllEvents(locals, actorUserId);
    const shows = selectedEventId ? await fetchShowsByEvent(locals, selectedEventId, actorUserId) : [];
    const balanceSheet = selectedShowId ? await fetchBalanceSheet(locals, actorUserId, selectedShowId) : null;
    const show = selectedShowId ? await fetchShow(locals, selectedShowId, actorUserId) : null;

    return {
        events,
        shows,
        selectedEventId,
        selectedShowId,
        balanceSheet,
        showName: show?.name ?? '',
        actorUserId
    };
};
