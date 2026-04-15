import type { PageServerLoad } from './$types';
import { env } from '$env/dynamic/public';

export const load: PageServerLoad = async ({ locals }) => {
    const res = await fetch(`${env.PUBLIC_BACKEND_URL ?? ''}/api/event/all`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${locals.session?.access_token}`
        },
        body: JSON.stringify({})
    });
    const events = res.ok ? await res.json() : [];
    return { events };
};
