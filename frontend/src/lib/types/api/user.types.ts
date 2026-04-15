import { z } from 'zod';
import { RoleEnum } from '$lib/types/role.type';

export const UserDtoSchema = z.object({
    id: z.string().uuid(),
    email: z.string().email(),
    roles: z.array(RoleEnum).optional().default([]),
    createdAt: z.string(),
});
export type UserDto = z.infer<typeof UserDtoSchema>;

export const UserIdsResponseSchema = z.union([
    z.object({
        userIds: z.array(z.string().uuid())
    }),
    z.array(z.string().uuid()).transform((userIds) => ({ userIds }))
]);
export type UserIdsResponse = z.infer<typeof UserIdsResponseSchema>;
