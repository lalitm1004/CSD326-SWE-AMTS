import type { PageServerLoad } from './$types';
import { fetchShow, fetchYearlyConsolidated } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const selectedYear = Number(url.searchParams.get('year') ?? new Date().getFullYear());
    const data = await fetchYearlyConsolidated(locals, actorUserId, selectedYear);
    const shows = await Promise.all(
        data.balanceSheets
            .filter((sheet) => !!sheet.showId)
            .map((sheet) => fetchShow(locals, sheet.showId!, actorUserId))
    );
    const showNames = Object.fromEntries(shows.filter(Boolean).map((show) => [show!.id, show!.name]));

    return { selectedYear, data, showNames };
};
