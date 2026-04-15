import { derived } from 'svelte/store';
import { UserStore } from './SupaStore';
import getCustomClaims from '$lib/utils/supabase/getCustomClaims';
import type { RoleEnumT } from '$lib/types/role.type';

export const rolesStore = derived(UserStore, ($user): RoleEnumT[] => {
    return getCustomClaims($user ?? null)?.roles ?? [];
});

export const hasRole = (roles: RoleEnumT[], ...required: RoleEnumT[]): boolean => {
    if (roles.includes('ROOT')) return true;
    return required.some(r => roles.includes(r));
};
