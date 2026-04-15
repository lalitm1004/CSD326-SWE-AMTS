import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchUser } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, params }) => {
    const [user, actor] = await Promise.all([
        fetchUser(locals, params.userId),
        locals.user?.id ? fetchUser(locals, locals.user.id) : Promise.resolve(null)
    ]);

    if (!user) {
        error(404, 'User not found');
    }

    return {
        user,
        actorUserId: locals.user?.id ?? '',
        actorRoles: actor?.roles ?? []
    };
};
