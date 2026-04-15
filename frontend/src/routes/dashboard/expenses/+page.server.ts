import type { PageServerLoad } from './$types';
import { fetchAllEvents, fetchBalanceSheet, fetchShow, fetchShowsByEvent, fetchUser } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const selectedEventId = url.searchParams.get('eventId') ?? '';
    const selectedShowId = url.searchParams.get('showId') ?? '';

    const [events, actor] = await Promise.all([
        fetchAllEvents(locals, actorUserId),
        actorUserId ? fetchUser(locals, actorUserId) : Promise.resolve(null)
    ]);

    const shows = selectedEventId ? await fetchShowsByEvent(locals, selectedEventId, actorUserId) : [];
    const balanceSheet = selectedShowId ? await fetchBalanceSheet(locals, actorUserId, selectedShowId) : null;
    const show = selectedShowId ? await fetchShow(locals, selectedShowId, actorUserId) : null;
    const actorRoles = actor?.roles ?? [];

    return {
        actorUserId,
        canEdit: actorRoles.some((role) => ['ROOT', 'FINANCIAL_CLERK'].includes(role)),
        events,
        shows,
        selectedEventId,
        selectedShowId,
        balanceSheet,
        showName: show?.name ?? ''
    };
};
