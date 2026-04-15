import type { PageServerLoad } from './$types';
import { backendJson, fetchShow, fetchUser, fetchUsersByIds } from '$lib/server/backend';
import {
    CommissionDtoSchema,
    CommissionResponseSchema,
    RevenueByShowResponseSchema,
    RevenueDtoSchema
} from '$lib/types/api/sales.types';
import { UserIdsResponseSchema } from '$lib/types/api/user.types';

export const load: PageServerLoad = async ({ locals, params }) => {
    const actorUserId = locals.user?.id ?? '';
    const [show, revenueResponse, salesAgentIds] = await Promise.all([
        fetchShow(locals, params.showId, actorUserId),
        backendJson<unknown>(locals, `api/sales/revenue/show?actorUserId=${actorUserId}&showId=${params.showId}`),
        backendJson<unknown>(locals, `api/user/sales-agents?actorUserId=${actorUserId}`)
    ]);

    const revenueValue = RevenueByShowResponseSchema.parse(revenueResponse).totalRevenue;
    const agents = await fetchUsersByIds(locals, UserIdsResponseSchema.parse(salesAgentIds).userIds);
    const commissions = await Promise.all(
        agents.map(async (agent) => {
            const commissionResponse = await backendJson<unknown>(
                locals,
                `api/sales/commission/agent/event?actorUserId=${actorUserId}&agentId=${agent.id}&eventId=${params.eventId}`
            );
            const { commission } = CommissionResponseSchema.parse(commissionResponse);
            return CommissionDtoSchema.parse({
                agentUserId: agent.id,
                agentEmail: agent.email,
                eventId: params.eventId,
                totalSales: 0,
                commission
            });
        })
    );

    return {
        show,
        revenues: [
            RevenueDtoSchema.parse({
                showId: params.showId,
                showName: show?.name ?? params.showId,
                totalRevenue: revenueValue
            })
        ],
        commissions
    };
};
