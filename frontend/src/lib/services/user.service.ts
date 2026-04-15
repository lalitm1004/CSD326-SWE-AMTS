import { fetchWithAuth } from '$lib/utils/fetchWithAuth';
import { UserDtoSchema, UserIdsResponseSchema, type UserDto } from '$lib/types/api/user.types';
import type { RoleEnumT } from '$lib/types/role.type';

export const getUserById = async (id: string): Promise<UserDto> => {
    const res = await fetchWithAuth(`api/user?id=${id}`);
    if (!res.ok) throw new Error(`User not found: ${res.status}`);
    return UserDtoSchema.parse(await res.json());
};

export const getUserByEmail = async (email: string): Promise<UserDto> => {
    const res = await fetchWithAuth(`api/user?email=${encodeURIComponent(email)}`);
    if (!res.ok) throw new Error(`User not found: ${res.status}`);
    return UserDtoSchema.parse(await res.json());
};

export const getClerks = async (actorUserId: string): Promise<UserDto[]> => {
    const res = await fetchWithAuth(`api/user/clerks?actorUserId=${actorUserId}`);
    if (!res.ok) throw new Error(`Failed to fetch clerks: ${res.status}`);
    const { userIds } = UserIdsResponseSchema.parse(await res.json());
    const users = await Promise.all(userIds.map((id) => getUserById(id)));
    return users;
};

export const getSalesAgents = async (actorUserId: string): Promise<UserDto[]> => {
    const res = await fetchWithAuth(`api/user/sales-agents?actorUserId=${actorUserId}`);
    if (!res.ok) throw new Error(`Failed to fetch sales agents: ${res.status}`);
    const { userIds } = UserIdsResponseSchema.parse(await res.json());
    const users = await Promise.all(userIds.map((id) => getUserById(id)));
    return users;
};

export const addRoles = async (actorUserId: string, targetUserId: string, roles: RoleEnumT[]): Promise<void> => {
    const res = await fetchWithAuth('api/user/roles', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ actorUserId, targetUserId, roles })
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error ?? `Failed to add roles: ${res.status}`);
    }
};

export const removeRoles = async (actorUserId: string, targetUserId: string, roles: RoleEnumT[]): Promise<void> => {
    const res = await fetchWithAuth('api/user/roles', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ actorUserId, targetUserId, roles })
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error ?? `Failed to remove roles: ${res.status}`);
    }
};
