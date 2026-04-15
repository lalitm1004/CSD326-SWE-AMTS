import type { PageServerLoad } from './$types';
import { backendJson, fetchAllEvents, fetchShowsByEvent } from '$lib/server/backend';
import { RevenueByEventResponseSchema, RevenueDtoSchema } from '$lib/types/api/sales.types';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const selectedEventId = url.searchParams.get('eventId') ?? '';
    const events = await fetchAllEvents(locals, actorUserId);

    if (!selectedEventId) {
        return { events, selectedEventId, revenues: [] };
    }

    const [shows, breakdown] = await Promise.all([
        fetchShowsByEvent(locals, selectedEventId, actorUserId),
        backendJson<unknown>(locals, `api/sales/revenue/event?actorUserId=${actorUserId}&eventId=${selectedEventId}`)
    ]);

    const revenues = RevenueByEventResponseSchema.parse(breakdown).items.map(({ showId, totalRevenue }) =>
        RevenueDtoSchema.parse({
            showId,
            showName: shows.find((show) => show.id === showId)?.name ?? showId,
            totalRevenue
        })
    );

    return { events, selectedEventId, revenues };
};
