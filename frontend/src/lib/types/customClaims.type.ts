import { z } from 'zod';

import { RoleEnum } from './role.type';

export const CustomClaimsSchema = z.object({
    roles: z.array(RoleEnum),
});

export type CustomClaimsT = z.infer<typeof CustomClaimsSchema>;