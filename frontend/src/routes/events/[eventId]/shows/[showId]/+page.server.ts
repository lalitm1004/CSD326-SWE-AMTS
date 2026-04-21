import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchShow, fetchShowSeats } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, params }) => {
    const userId = locals.user?.id;
    const show = await fetchShow(locals, params.showId, userId);

    if (!show) {
        error(404, 'Show not found');
    }

    const seats = userId
        ? await fetchShowSeats(locals, params.showId, userId)
        : [];

    return { show, seats, eventId: params.eventId };
};
