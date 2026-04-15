import type { PageServerLoad } from './$types';
import { z } from 'zod';
import { backendJson } from '$lib/server/backend';
import getCustomClaims from '$lib/utils/supabase/getCustomClaims';
import { SpectatorBookingSummaryDtoSchema, type SpectatorBookingSummaryDto } from '$lib/types/api/ticket.types';

export const load: PageServerLoad = async ({ locals }) => {
    if (!locals.user) {
        return { bookings: [] as SpectatorBookingSummaryDto[] };
    }

    const roles = getCustomClaims(locals.user)?.roles ?? [];
    if (!roles.includes('SPECTATOR')) {
        return { bookings: [] as SpectatorBookingSummaryDto[] };
    }

    const bookings = await backendJson<unknown>(locals, `api/ticket/bookings?spectatorUserId=${locals.user.id}`);

    return {
        bookings: z.array(SpectatorBookingSummaryDtoSchema).parse(bookings)
    };
};
