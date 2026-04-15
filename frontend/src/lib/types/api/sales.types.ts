import { z } from 'zod';

export const RevenueDtoSchema = z.object({
    showId: z.string().uuid(),
    showName: z.string().optional(),
    totalRevenue: z.number(),
    ordinaryRevenue: z.number().optional(),
    balconyRevenue: z.number().optional(),
    ticketsSold: z.number().optional(),
});
export type RevenueDto = z.infer<typeof RevenueDtoSchema>;

export const RevenueByShowResponseSchema = z.object({
    totalRevenue: z.number()
});
export type RevenueByShowResponse = z.infer<typeof RevenueByShowResponseSchema>;

export const RevenueBreakdownItemSchema = z.object({
    showId: z.string().uuid(),
    totalRevenue: z.number()
});
export type RevenueBreakdownItem = z.infer<typeof RevenueBreakdownItemSchema>;

export const RevenueByEventResponseSchema = z.object({
    items: z.array(RevenueBreakdownItemSchema)
});
export type RevenueByEventResponse = z.infer<typeof RevenueByEventResponseSchema>;

export const CommissionDtoSchema = z.object({
    agentUserId: z.string().uuid(),
    agentEmail: z.string().optional(),
    totalSales: z.number(),
    commission: z.number(),
    showId: z.string().uuid().optional(),
    eventId: z.string().uuid().optional(),
});
export type CommissionDto = z.infer<typeof CommissionDtoSchema>;

export const CommissionResponseSchema = z.object({
    commission: z.number()
});
export type CommissionResponse = z.infer<typeof CommissionResponseSchema>;
