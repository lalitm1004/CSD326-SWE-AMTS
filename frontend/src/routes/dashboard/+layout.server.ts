import { redirect } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';
import getCustomClaims from '$lib/utils/supabase/getCustomClaims';

const ADMIN_ROLES = ['ROOT', 'PRESIDENT', 'AUDITORIUM_SECRETARY', 'SHOW_MANAGER', 'SALES_AGENT', 'FINANCIAL_CLERK'];

export const load: LayoutServerLoad = async ({ locals }) => {
    if (!locals.session || !locals.user) {
        redirect(302, '/auth');
    }

    const claims = getCustomClaims(locals.user);
    const roles = claims?.roles ?? [];
    const hasAdminRole = roles.some(r => ADMIN_ROLES.includes(r));

    if (!hasAdminRole) {
        redirect(302, '/');
    }

    return { roles };
};
