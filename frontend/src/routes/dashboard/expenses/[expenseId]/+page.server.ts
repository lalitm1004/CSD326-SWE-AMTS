import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchBalanceSheet, fetchShow, fetchUser } from '$lib/server/backend';

export const load: PageServerLoad = async ({ locals, params, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const showId = url.searchParams.get('showId') ?? '';

    if (!showId) {
        error(400, 'showId query parameter is required');
    }

    const [actor, show, balanceSheet] = await Promise.all([
        actorUserId ? fetchUser(locals, actorUserId) : Promise.resolve(null),
        fetchShow(locals, showId, actorUserId),
        fetchBalanceSheet(locals, actorUserId, showId)
    ]);

    const expense = balanceSheet?.expenses.find((item) => item.id === params.expenseId);
    if (!expense) {
        error(404, 'Expense not found');
    }

    return {
        actorUserId,
        canEdit: (actor?.roles ?? []).some((role) => ['ROOT', 'FINANCIAL_CLERK'].includes(role)),
        showId,
        showName: show?.name ?? 'Show',
        expense
    };
};
