import { z } from 'zod';

export const RoleEnum = z.enum([
    "ROOT",
    "PRESIDENT",
    "AUDITORIUM_SECRETARY",
    "SHOW_MANAGER",
    "SALES_AGENT",
    "FINANCIAL_CLERK",
    "SPECTATOR",
]);

export type RoleEnumT = z.infer<typeof RoleEnum>;