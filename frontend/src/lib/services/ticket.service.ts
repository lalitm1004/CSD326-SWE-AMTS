import { fetchWithAuth } from '$lib/utils/fetchWithAuth';
import { z } from 'zod';
import {
    CancelTicketsResponseSchema,
    PurchaseCouponResponseSchema,
    TicketDtoSchema,
    type TicketDto
} from '$lib/types/api/ticket.types';

const jsonPost = (route: string, body: unknown) =>
    fetchWithAuth(route, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

export const purchaseTicket = async (data: {
    spectatorUserId: string;
    showId: string;
    seatIds: string[];
    couponCode?: string;
}): Promise<TicketDto[]> => {
    const res = await jsonPost('api/ticket/purchase', data);
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error ?? `Purchase failed: ${res.status}`);
    }
    return z.array(TicketDtoSchema).parse(await res.json());
};

export const purchaseTicketViaAgent = async (data: {
    spectatorUserId: string;
    salesAgentUserId: string;
    showId: string;
    seatIds: string[];
}): Promise<TicketDto[]> => {
    const res = await jsonPost('api/ticket/purchase/agent', {
        agentUserId: data.salesAgentUserId,
        spectatorUserId: data.spectatorUserId,
        showId: data.showId,
        seatIds: data.seatIds
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error ?? `Purchase failed: ${res.status}`);
    }
    return z.array(TicketDtoSchema).parse(await res.json());
};

export const purchaseCoupon = async (spectatorUserId: string, showId: string): Promise<string> => {
    const res = await fetchWithAuth(`api/ticket/coupon?spectatorUserId=${spectatorUserId}&showId=${showId}`, {
        method: 'POST'
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error ?? `Coupon purchase failed: ${res.status}`);
    }
    return PurchaseCouponResponseSchema.parse(await res.json()).code;
};

export const cancelTickets = async (data: {
    spectatorUserId: string;
    ticketIds: string[];
}): Promise<number> => {
    const res = await jsonPost('api/ticket/cancel', data);
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error ?? `Cancellation failed: ${res.status}`);
    }
    return CancelTicketsResponseSchema.parse(await res.json()).refund;
};

export const getTicketsByBooking = async (bookingId: string): Promise<TicketDto[]> => {
    const res = await fetchWithAuth(`api/ticket?bookingId=${bookingId}`);
    if (!res.ok) throw new Error(`Failed to fetch tickets: ${res.status}`);
    return z.array(TicketDtoSchema).parse(await res.json());
};
