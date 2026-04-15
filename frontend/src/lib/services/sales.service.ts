import { fetchWithAuth } from '$lib/utils/fetchWithAuth';
import {
    CommissionDtoSchema,
    CommissionResponseSchema,
    RevenueByEventResponseSchema,
    RevenueByShowResponseSchema,
    RevenueDtoSchema,
    type RevenueDto,
    type CommissionDto
} from '$lib/types/api/sales.types';

export const getRevenueByShow = async (userId: string, showId: string): Promise<RevenueDto> => {
    const res = await fetchWithAuth(`api/sales/revenue/show?actorUserId=${userId}&showId=${showId}`);
    if (!res.ok) throw new Error(`Failed to fetch revenue: ${res.status}`);
    const { totalRevenue } = RevenueByShowResponseSchema.parse(await res.json());
    return RevenueDtoSchema.parse({
        showId,
        totalRevenue
    });
};

export const getRevenueByEvent = async (userId: string, eventId: string): Promise<RevenueDto[]> => {
    const res = await fetchWithAuth(`api/sales/revenue/event?actorUserId=${userId}&eventId=${eventId}`);
    if (!res.ok) throw new Error(`Failed to fetch revenue: ${res.status}`);
    const { items } = RevenueByEventResponseSchema.parse(await res.json());
    return items.map(({ showId, totalRevenue }) =>
        RevenueDtoSchema.parse({ showId, totalRevenue })
    );
};

export const getAgentCommissions = async (actorUserId: string, agentUserId: string): Promise<CommissionDto[]> => {
    const res = await fetchWithAuth(`api/sales/commission/agent?actorUserId=${actorUserId}&agentId=${agentUserId}`);
    if (!res.ok) throw new Error(`Failed to fetch commissions: ${res.status}`);
    const { commission } = CommissionResponseSchema.parse(await res.json());
    return [
        CommissionDtoSchema.parse({
            agentUserId,
            totalSales: 0,
            commission
        })
    ];
};

export const getCommissionsByEvent = async (userId: string, agentUserId: string, eventId: string): Promise<CommissionDto[]> => {
    const res = await fetchWithAuth(`api/sales/commission/agent/event?actorUserId=${userId}&agentId=${agentUserId}&eventId=${eventId}`);
    if (!res.ok) throw new Error(`Failed to fetch commissions: ${res.status}`);
    const { commission } = CommissionResponseSchema.parse(await res.json());
    return [
        CommissionDtoSchema.parse({
            agentUserId,
            eventId,
            totalSales: 0,
            commission
        })
    ];
};
