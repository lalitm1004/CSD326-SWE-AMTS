import type { PageServerLoad } from './$types';
import { fetchUser, backendJson } from '$lib/server/backend';
import { UserDtoSchema } from '$lib/types/api/user.types';
import { z } from 'zod';

export const load: PageServerLoad = async ({ locals }) => {
    const actorUserId = locals.user?.id;
    if (!actorUserId) {
        return { users: [] };
    }

    const [usersResponse, actor] = await Promise.all([
        backendJson<unknown>(locals, `api/user/all?actorUserId=${actorUserId}`),
        fetchUser(locals, actorUserId)
    ]);

    const users = z.array(UserDtoSchema).parse(usersResponse);

    return {
        actorUserId,
        actorRoles: actor?.roles ?? [],
        users: users.sort((a, b) => a.email.localeCompare(b.email))
    };
};
