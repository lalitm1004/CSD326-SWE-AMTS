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

export const EventDtoSchema = z.object({
    id: z.string().uuid(),
    createdByUserId: z.string().uuid().nullable().optional(),
    name: z.string(),
    description: z.string().nullable().optional(),
    thumbnailUrl: z.string().nullable().optional(),
    startingAt: z.string().nullable().optional(),
    endingAt: z.string().nullable().optional(),
    createdAt: z.string(),
});

export type EventDto = z.infer<typeof EventDtoSchema>;

export const ShowDtoSchema = z.object({
    id: z.string().uuid(),
    eventId: z.string().uuid(),
    createdByUserId: z.string().uuid().nullable().optional(),
    name: z.string(),
    description: z.string().nullable().optional(),
    thumbnailUrl: z.string().nullable().optional(),
    startingAt: z.string().nullable().optional(),
    endingAt: z.string().nullable().optional(),
    ordinarySeatPrice: MoneyLikeSchema.nullable().optional(),
    balconySeatPrice: MoneyLikeSchema.nullable().optional(),
    ordinarySeatCount: z.number().nullable().optional(),
    balconySeatCount: z.number().nullable().optional(),
    numOrdinarySeats: z.number().nullable().optional(),
    numBalconySeats: z.number().nullable().optional(),
    createdAt: z.string(),
}).transform((show) => ({
    ...show,
    ordinarySeatCount: show.ordinarySeatCount ?? show.numOrdinarySeats,
    balconySeatCount: show.balconySeatCount ?? show.numBalconySeats
}));

export type ShowDto = z.infer<typeof ShowDtoSchema>;
