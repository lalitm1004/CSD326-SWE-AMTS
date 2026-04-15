import { z } from 'zod';

export const SeatTypeEnum = z.enum(['BALCONY', 'ORDINARY']);
export type SeatTypeT = z.infer<typeof SeatTypeEnum>;

export const SeatDesignationEnum = z.enum(['ORDINARY', 'VIP', 'COMPLIMENTARY']);
export type SeatDesignationT = z.infer<typeof SeatDesignationEnum>;

export const SeatDtoSchema = z.object({
    id: z.string().uuid(),
    number: z.string().optional(),
    row: z.string().optional(),
    col: z.number().optional(),
    type: SeatTypeEnum,
    designation: SeatDesignationEnum.optional().default('ORDINARY'),
    isAvailable: z.boolean().optional(),
    available: z.boolean().optional()
}).transform((seat) => {
    const seatNumber = seat.number ?? '';
    const match = /^([A-Za-z]+)(\d+)$/.exec(seatNumber);

    return {
        ...seat,
        row: seat.row ?? match?.[1],
        col: seat.col ?? (match?.[2] ? Number(match[2]) : undefined),
        isAvailable: seat.isAvailable ?? seat.available ?? true
    };
});

export type SeatDto = z.infer<typeof SeatDtoSchema>;
