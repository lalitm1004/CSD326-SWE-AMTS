import { redirect, error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchShow, fetchShowSeats } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, params }) => {
    if (!locals.session || !locals.user) {
        redirect(302, '/auth');
    }

    const userId = locals.user.id;
    const [show, seats] = await Promise.all([
        fetchShow(locals, params.showId, userId),
        fetchShowSeats(locals, params.showId, userId)
    ]);

    if (!show) {
        error(404, 'Show not found');
    }

    return { show, seats, eventId: params.eventId };
};
