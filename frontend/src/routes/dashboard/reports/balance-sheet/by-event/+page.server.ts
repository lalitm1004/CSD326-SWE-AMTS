import type { PageServerLoad } from './$types';
import { fetchAllEvents, fetchBalanceSheetsByEvent, fetchShow } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const selectedEventId = url.searchParams.get('eventId') ?? '';
    const events = await fetchAllEvents(locals, actorUserId);

    if (!selectedEventId) {
        return { events, selectedEventId, sheets: [], showNames: {}, actorUserId };
    }

    const sheets = await fetchBalanceSheetsByEvent(locals, actorUserId, selectedEventId);
    const shows = await Promise.all(
        sheets
            .filter((sheet) => !!sheet.showId)
            .map((sheet) => fetchShow(locals, sheet.showId!, actorUserId))
    );
    const showNames = Object.fromEntries(shows.filter(Boolean).map((show) => [show!.id, show!.name]));

    return { events, selectedEventId, sheets, showNames, actorUserId };
};
