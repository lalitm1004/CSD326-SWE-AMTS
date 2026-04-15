<script lang="ts">
    import Badge from '$lib/components/ui/Badge.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import { addRoles, removeRoles } from '$lib/services/user.service';
    import { toastStore } from '$lib/stores/toast.store';
    import type { UserDto } from '$lib/types/api/user.types';
    import { RoleEnum, type RoleEnumT } from '$lib/types/role.type';
    import { invalidateAll } from '$app/navigation';

    interface Props {
        user: UserDto;
        actorUserId: string;
        actorRoles: RoleEnumT[];
    }

    const ALL_ROLES = RoleEnum.options;

    let { user, actorUserId, actorRoles }: Props = $props();

    let selectedRoles = $state<RoleEnumT[]>([]);
    let saving = $state(false);

    $effect(() => {
        selectedRoles = [...user.roles];
    });

    function prettify(role: RoleEnumT): string {
        return role.toLowerCase().replaceAll('_', ' ');
    }

    function isEditable(role: RoleEnumT): boolean {
        if (role === 'ROOT' || role === 'SPECTATOR') return false;
        if (actorRoles.includes('ROOT')) return true;
        if (actorRoles.includes('PRESIDENT')) return role === 'AUDITORIUM_SECRETARY';
        if (actorRoles.includes('AUDITORIUM_SECRETARY')) {
            return !['AUDITORIUM_SECRETARY', 'ROOT'].includes(role);
        }
        return false;
    }

    function toggle(role: RoleEnumT, checked: boolean) {
        if (checked) {
            selectedRoles = Array.from(new Set([...selectedRoles, role]));
        } else {
            selectedRoles = selectedRoles.filter((item) => item !== role);
        }
    }

    async function handleSave() {
        const original = new Set(user.roles);
        const current = new Set(selectedRoles);

        const toAdd = ALL_ROLES.filter((role) => current.has(role) && !original.has(role) && isEditable(role));
        const toRemove = ALL_ROLES.filter((role) => original.has(role) && !current.has(role) && isEditable(role));

        if (toAdd.length === 0 && toRemove.length === 0) {
            toastStore.add('info', 'No role changes to save.');
            return;
        }

        saving = true;
        try {
            if (toAdd.length > 0) {
                await addRoles(actorUserId, user.id, toAdd);
            }
            if (toRemove.length > 0) {
                await removeRoles(actorUserId, user.id, toRemove);
            }
            toastStore.add('success', 'Roles updated.');
            await invalidateAll();
        } catch (error: unknown) {
            toastStore.add('error', (error as Error).message ?? 'Failed to update roles');
        } finally {
            saving = false;
        }
    }
</script>

<div class="corner-ornament border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
    <div class="mb-5">
        <p class="mb-2 text-[10px] uppercase tracking-widest text-[var(--color-text-muted)]">Current Roles</p>
        <div class="flex flex-wrap gap-2">
            {#each selectedRoles as role}
                <Badge variant={role}>{role}</Badge>
            {/each}
        </div>
    </div>

    <div class="grid gap-3 sm:grid-cols-2">
        {#each ALL_ROLES as role}
            {@const checked = selectedRoles.includes(role)}
            {@const editable = isEditable(role)}
            <label class="flex items-start gap-3 border border-[var(--color-border)] bg-[var(--color-raised)] px-4 py-3">
                <input
                    type="checkbox"
                    checked={checked}
                    disabled={!editable || saving}
                    onchange={(event) => toggle(role, (event.currentTarget as HTMLInputElement).checked)}
                    class="mt-1 accent-[var(--color-gold)]"
                />
                <div class="min-w-0">
                    <p class="text-sm uppercase tracking-wide text-[var(--color-text)]">{prettify(role)}</p>
                    <p class="text-xs text-[var(--color-text-muted)]">
                        {editable ? 'Editable by your current role set.' : 'Read-only for your current permissions.'}
                    </p>
                </div>
            </label>
        {/each}
    </div>

    <div class="mt-5 flex justify-end">
        <Button variant="primary" onclick={handleSave} loading={saving}>Save Roles</Button>
    </div>
</div>
