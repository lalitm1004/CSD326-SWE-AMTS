import type { PageServerLoad } from './$types';
import { fetchAllEvents } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals }) => {
    try {
        const events = await fetchAllEvents(locals, locals.user?.id);
        return { events, loading: false, error: null };
    } catch {
        return { events: [], loading: false, error: 'Could not load events.' };
    }
};
