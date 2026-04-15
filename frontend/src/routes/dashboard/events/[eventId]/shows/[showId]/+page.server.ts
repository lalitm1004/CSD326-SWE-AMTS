import type { PageServerLoad } from './$types';
import { env } from '$env/dynamic/public';

export const load: PageServerLoad = async ({ locals, params }) => {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${locals.session?.access_token}`
    };
    const [showRes, seatsRes] = await Promise.all([
        fetch(`${env.PUBLIC_BACKEND_URL ?? ''}/api/show/get`, { method: 'POST', headers, body: JSON.stringify({ showId: params.showId }) }),
        fetch(`${env.PUBLIC_BACKEND_URL ?? ''}/api/show/seats`, { method: 'POST', headers, body: JSON.stringify({ showId: params.showId }) })
    ]);
    const show = await showRes.json();
    const seats = seatsRes.ok ? await seatsRes.json() : [];
    return { show, seats, eventId: params.eventId };
};
