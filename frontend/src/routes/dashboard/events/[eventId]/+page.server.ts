import type { PageServerLoad } from './$types';
import { env } from '$env/dynamic/public';

export const load: PageServerLoad = async ({ locals, params }) => {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${locals.session?.access_token}`
    };
    const [eventRes, showsRes] = await Promise.all([
        fetch(`${env.PUBLIC_BACKEND_URL ?? ''}/api/event/get`, { method: 'POST', headers, body: JSON.stringify({ eventId: params.eventId }) }),
        fetch(`${env.PUBLIC_BACKEND_URL ?? ''}/api/show/by-event`, { method: 'POST', headers, body: JSON.stringify({ eventId: params.eventId }) })
    ]);
    const event = await eventRes.json();
    const shows = showsRes.ok ? await showsRes.json() : [];
    return { event, shows };
};
