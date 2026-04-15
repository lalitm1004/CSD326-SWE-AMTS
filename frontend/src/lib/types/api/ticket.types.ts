import { z } from 'zod';

const MoneyLikeSchema = z.union([
    z.number(),
    z.null(),
    z.object({
        amount: z.number()
    })
]).transform((value) => {
    if (value === null) return null;
    return typeof value === 'number' ? value : value.amount;
});

export const BookingTypeEnum = z.enum(['ONLINE', 'OFFLINE', 'COMPLEMENTARY']);
export type BookingTypeT = z.infer<typeof BookingTypeEnum>;

export const RefundTypeEnum = z.enum(['BEFORE_THREE_DAYS', 'BEFORE_ONE_DAY', 'SAME_DAY']);

export const BookingDtoSchema = z.object({
    id: z.string().uuid(),
    showId: z.string().uuid().nullable().optional(),
    code: z.string(),
    type: BookingTypeEnum,
    amount: MoneyLikeSchema.optional(),
    createdAt: z.string(),
    spectatorUserId: z.string().uuid().nullable().optional(),
    salesAgentUserId: z.string().uuid().nullable().optional(),
    couponId: z.string().uuid().nullable().optional(),
    createdByUserId: z.string().uuid().nullable().optional(),
});
export type BookingDto = z.infer<typeof BookingDtoSchema>;

export const SpectatorBookingSummaryDtoSchema = BookingDtoSchema.extend({
    ticketCount: z.number().int().nonnegative(),
    refundedTicketCount: z.number().int().nonnegative()
});
export type SpectatorBookingSummaryDto = z.infer<typeof SpectatorBookingSummaryDtoSchema>;

export const TicketDtoSchema = z.object({
    id: z.string().uuid(),
    bookingId: z.string().uuid().nullable().optional(),
    showId: z.string().uuid().nullable().optional(),
    seatId: z.string().uuid().nullable().optional(),
    code: z.string(),
    isRefunded: z.boolean().optional(),
    refunded: z.boolean().optional(),
    createdAt: z.string(),
}).transform((ticket) => ({
    ...ticket,
    isRefunded: ticket.isRefunded ?? ticket.refunded ?? false
}));
export type TicketDto = z.infer<typeof TicketDtoSchema>;

export const CouponDtoSchema = z.object({
    id: z.string().uuid(),
    code: z.string(),
    spectatorUserId: z.string().uuid().nullable().optional(),
    showId: z.string().uuid().nullable().optional(),
    createdAt: z.string(),
});
export type CouponDto = z.infer<typeof CouponDtoSchema>;

export const PurchaseCouponResponseSchema = z.object({
    code: z.string()
});
export type PurchaseCouponResponse = z.infer<typeof PurchaseCouponResponseSchema>;

export const CancelTicketsResponseSchema = z.object({
    refund: z.number()
});
export type CancelTicketsResponse = z.infer<typeof CancelTicketsResponseSchema>;
