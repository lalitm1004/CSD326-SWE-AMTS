import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';
import getCustomClaims from '$lib/utils/supabase/getCustomClaims';

export const load: LayoutServerLoad = async ({ locals, parent }) => {
    await parent();

    const claims = getCustomClaims(locals.user);
    const roles = claims?.roles ?? [];
    if (!roles.some((role) => ['ROOT', 'PRESIDENT'].includes(role))) {
        redirect(302, '/dashboard');
    }

    return {};
};
