import { writable } from 'svelte/store';

export interface CartState {
    showId: string | null;
    selectedSeats: string[];
    couponId: string | undefined;
}

const initial: CartState = { showId: null, selectedSeats: [], couponId: undefined };

const { subscribe, set, update } = writable<CartState>(initial);

export const cartStore = {
    subscribe,
    setShow(showId: string) {
        set({ showId, selectedSeats: [], couponId: undefined });
    },
    toggleSeat(seatId: string) {
        update(s => ({
            ...s,
            selectedSeats: s.selectedSeats.includes(seatId)
                ? s.selectedSeats.filter(id => id !== seatId)
                : [...s.selectedSeats, seatId]
        }));
    },
    setCoupon(couponId: string | undefined) {
        update(s => ({ ...s, couponId }));
    },
    clear() {
        set(initial);
    }
};
