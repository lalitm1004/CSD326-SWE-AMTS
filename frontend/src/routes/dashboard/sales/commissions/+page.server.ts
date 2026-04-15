import type { PageServerLoad } from './$types';
import { backendJson, fetchUser } from '$lib/server/backend';
import { CommissionDtoSchema, CommissionResponseSchema } from '$lib/types/api/sales.types';

export const load: PageServerLoad = async ({ locals }) => {
    const actorUserId = locals.user?.id ?? '';
    const [actor, commission] = await Promise.all([
        actorUserId ? fetchUser(locals, actorUserId) : Promise.resolve(null),
        actorUserId
            ? backendJson<unknown>(locals, `api/sales/commission/agent?actorUserId=${actorUserId}&agentId=${actorUserId}`)
            : Promise.resolve({ commission: 0 })
    ]);

    const totalCommission = CommissionResponseSchema.parse(commission).commission;

    const row = CommissionDtoSchema.parse({
        agentUserId: actorUserId,
        agentEmail: actor?.email ?? 'Current agent',
        totalSales: 0,
        commission: totalCommission
    });

    return {
        commissions: actorUserId ? [row] : [],
        totalCommission
    };
};
