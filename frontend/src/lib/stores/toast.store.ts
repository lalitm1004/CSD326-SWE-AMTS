import { writable } from 'svelte/store';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface Toast {
    id: string;
    type: ToastType;
    message: string;
}

const { subscribe, update } = writable<Toast[]>([]);

export const toastStore = {
    subscribe,
    add(type: ToastType, message: string) {
        const id = crypto.randomUUID();
        update(toasts => [...toasts, { id, type, message }]);
        setTimeout(() => this.dismiss(id), 4000);
    },
    dismiss(id: string) {
        update(toasts => toasts.filter(t => t.id !== id));
    }
};
