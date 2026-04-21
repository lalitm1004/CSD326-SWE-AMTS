import { fetchWithAuth } from '$lib/utils/fetchWithAuth';
import { get } from 'svelte/store';
import { UserStore } from '$lib/stores/SupaStore';
import { z } from 'zod';
import { ShowDtoSchema, type ShowDto } from '$lib/types/api/event.types';
import { SeatDtoSchema, type SeatDto } from '$lib/types/api/seat.types';

export const DEFAULT_ORDINARY_SEAT_PRICE = 500;
export const DEFAULT_BALCONY_SEAT_PRICE = 800;

const jsonPost = (route: string, body: unknown) =>
    fetchWithAuth(route, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

const jsonPatch = (route: string, body: unknown) =>
    fetchWithAuth(route, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

export const getShow = async (showId: string): Promise<ShowDto> => {
    const res = await jsonPost('api/show/get', { userId: get(UserStore)?.id, showId });
    if (!res.ok) throw new Error(`Failed to fetch show: ${res.status}`);
    return ShowDtoSchema.parse(await res.json());
};

export const getShowForUser = async (showId: string, userId?: string): Promise<ShowDto> => {
    const res = await jsonPost('api/show/get', { userId, showId });
    if (!res.ok) throw new Error(`Failed to fetch show: ${res.status}`);
    return ShowDtoSchema.parse(await res.json());
};

export const getShowSeats = async (showId: string): Promise<SeatDto[]> => {
    const res = await jsonPost('api/show/seats', { userId: get(UserStore)?.id, showId });
    if (!res.ok) throw new Error(`Failed to fetch seats: ${res.status}`);
    return z.array(SeatDtoSchema).parse(await res.json());
};

export const getShowSeatsForUser = async (showId: string, userId?: string): Promise<SeatDto[]> => {
    const res = await jsonPost('api/show/seats', { userId, showId });
    if (!res.ok) throw new Error(`Failed to fetch seats: ${res.status}`);
    return z.array(SeatDtoSchema).parse(await res.json());
};

export const createShow = async (data: {
    eventId: string;
    creatorUserId: string;
    name: string;
    description?: string;
    thumbnailUrl?: string;
    startingAt?: string;
    endingAt?: string;
    ordinarySeatPrice?: number;
    balconySeatPrice?: number;
    ordinarySeatCount?: number;
    balconySeatCount?: number;
}): Promise<void> => {
    const res = await jsonPost('api/show', {
        createdByUserId: data.creatorUserId,
        eventId: data.eventId,
        name: data.name,
        description: data.description,
        thumbnailUrl: data.thumbnailUrl,
        startingAt: data.startingAt,
        endingAt: data.endingAt,
        ordinarySeatPrice: data.ordinarySeatPrice ?? DEFAULT_ORDINARY_SEAT_PRICE,
        balconySeatPrice: data.balconySeatPrice ?? DEFAULT_BALCONY_SEAT_PRICE,
        numOrdinarySeats: data.ordinarySeatCount ?? 0,
        numBalconySeats: data.balconySeatCount ?? 0
    });
    if (!res.ok) throw new Error(`Failed to create show: ${res.status}`);
};

export const deleteShow = async (showId: string, actorUserId: string): Promise<void> => {
    const res = await fetchWithAuth('api/show', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ showId, userId: actorUserId })
    });
    if (!res.ok) throw new Error(`Failed to delete show: ${res.status}`);
};

export const updateShowName = async (showId: string, name: string, actorUserId: string) => {
    const res = await jsonPatch('api/show/name', { showId, newName: name, userId: actorUserId });
    if (!res.ok) throw new Error(`Failed to update show name: ${res.status}`);
};

export const updateShowDescription = async (showId: string, description: string, actorUserId: string) => {
    const res = await jsonPatch('api/show/description', { showId, newDescription: description, userId: actorUserId });
    if (!res.ok) throw new Error(`Failed to update show description: ${res.status}`);
};

export const updateShowSeatPrices = async (showId: string, actorUserId: string, ordinarySeatPrice: number, balconySeatPrice: number) => {
    const ordinaryRes = await jsonPatch('api/show/ordinary-seat-price', { showId, userId: actorUserId, newOrdinarySeatPrice: ordinarySeatPrice });
    if (!ordinaryRes.ok) throw new Error(`Failed to update ordinary seat price: ${ordinaryRes.status}`);

    const balconyRes = await jsonPatch('api/show/balcony-seat-price', { showId, userId: actorUserId, newBalconySeatPrice: balconySeatPrice });
    if (!balconyRes.ok) throw new Error(`Failed to update balcony seat price: ${balconyRes.status}`);
};

export const updateShowSeatCounts = async (showId: string, actorUserId: string, ordinarySeatCount: number, balconySeatCount: number) => {
    const ordinaryRes = await jsonPatch('api/show/num-ordinary-seats', { showId, userId: actorUserId, newNumOrdinarySeats: ordinarySeatCount });
    if (!ordinaryRes.ok) throw new Error(`Failed to update ordinary seat count: ${ordinaryRes.status}`);

    const balconyRes = await jsonPatch('api/show/num-balcony-seats', { showId, userId: actorUserId, newNumBalconySeats: balconySeatCount });
    if (!balconyRes.ok) throw new Error(`Failed to update balcony seat count: ${balconyRes.status}`);
};

export const updateShowStartingAt = async (showId: string, actorUserId: string, startingAt: string) => {
    const res = await jsonPatch('api/show/starting-at', { showId, userId: actorUserId, newStartingAt: startingAt });
    if (!res.ok) throw new Error(`Failed to update show start time: ${res.status}`);
};

export const updateShowEndingAt = async (showId: string, actorUserId: string, endingAt: string) => {
    const res = await jsonPatch('api/show/ending-at', { showId, userId: actorUserId, newEndingAt: endingAt });
    if (!res.ok) throw new Error(`Failed to update show end time: ${res.status}`);
};

export const updateSeatDesignation = async (_showId: string, actorUserId: string, seatId: string, designation: 'VIP' | 'COMPLIMENTARY' | 'ORDINARY') => {
    const res = await jsonPatch('api/show/seat-designation', { seatId, designation, userId: actorUserId });
    if (!res.ok) throw new Error(`Failed to update seat designation: ${res.status}`);
};
