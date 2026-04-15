import type { PageServerLoad } from './$types';
import { backendJson, fetchAllEvents, fetchUser, fetchUsersByIds } from '$lib/server/backend';
import { CommissionDtoSchema, CommissionResponseSchema } from '$lib/types/api/sales.types';
import { UserIdsResponseSchema } from '$lib/types/api/user.types';

export const load: PageServerLoad = async ({ locals, url }) => {
    const actorUserId = locals.user?.id ?? '';
    const selectedEventId = url.searchParams.get('eventId') ?? '';
    const events = await fetchAllEvents(locals, actorUserId);

    if (!selectedEventId) {
        return { events, selectedEventId, commissions: [] };
    }

    const ids = await backendJson<unknown>(locals, `api/user/sales-agents?actorUserId=${actorUserId}`);
    const agentIds = UserIdsResponseSchema.parse(ids).userIds;
    const agents = await fetchUsersByIds(locals, agentIds);

    const commissions = await Promise.all(
        agents.map(async (agent) => {
            const commissionResponse = await backendJson<unknown>(
                locals,
                `api/sales/commission/agent/event?actorUserId=${actorUserId}&agentId=${agent.id}&eventId=${selectedEventId}`
            );
            const { commission } = CommissionResponseSchema.parse(commissionResponse);
            return CommissionDtoSchema.parse({
                agentUserId: agent.id,
                agentEmail: agent.email,
                eventId: selectedEventId,
                totalSales: 0,
                commission
            });
        })
    );

    return { events, selectedEventId, commissions };
};
