import { fetchWithAuth } from '$lib/utils/fetchWithAuth';
import { get } from 'svelte/store';
import { UserStore } from '$lib/stores/SupaStore';
import { z } from 'zod';
import { EventDtoSchema, ShowDtoSchema, type EventDto, type ShowDto } from '$lib/types/api/event.types';

const json = (route: string, body: unknown) =>
    fetchWithAuth(route, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

export const getAllEvents = async (): Promise<EventDto[]> => {
    const res = await json('api/event/all', {});
    if (!res.ok) throw new Error(`Failed to fetch events: ${res.status}`);
    return z.array(EventDtoSchema).parse(await res.json());
};

export const getEventById = async (id: string): Promise<EventDto> => {
    const res = await json('api/event/get', { userId: get(UserStore)?.id, eventId: id });
    if (!res.ok) throw new Error(`Failed to fetch event: ${res.status}`);
    return EventDtoSchema.parse(await res.json());
};

export const getEventByIdForUser = async (id: string, userId?: string): Promise<EventDto> => {
    const res = await json('api/event/get', { userId, eventId: id });
    if (!res.ok) throw new Error(`Failed to fetch event: ${res.status}`);
    return EventDtoSchema.parse(await res.json());
};

export const createEvent = async (data: { name: string; description?: string; thumbnailUrl?: string; creatorUserId: string }): Promise<void> => {
    const res = await json('api/event', {
        createdByUserId: data.creatorUserId,
        name: data.name,
        description: data.description,
        thumbnailUrl: data.thumbnailUrl
    });
    if (!res.ok) throw new Error(`Failed to create event: ${res.status}`);
};

export const deleteEvent = async (eventId: string, actorUserId: string): Promise<void> => {
    const res = await fetchWithAuth('api/event', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ eventId, userId: actorUserId })
    });
    if (!res.ok) throw new Error(`Failed to delete event: ${res.status}`);
};

export const updateEventName = async (eventId: string, name: string, actorUserId: string): Promise<void> => {
    const res = await fetchWithAuth('api/event/name', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ eventId, newName: name, userId: actorUserId })
    });
    if (!res.ok) throw new Error(`Failed to update event: ${res.status}`);
};

export const updateEventDescription = async (eventId: string, description: string, actorUserId: string): Promise<void> => {
    const res = await fetchWithAuth('api/event/description', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ eventId, newDescription: description, userId: actorUserId })
    });
    if (!res.ok) throw new Error(`Failed to update event description: ${res.status}`);
};

export const getShowsByEvent = async (eventId: string): Promise<ShowDto[]> => {
    const res = await json('api/show/by-event', { userId: get(UserStore)?.id, eventId });
    if (!res.ok) throw new Error(`Failed to fetch shows: ${res.status}`);
    return z.array(ShowDtoSchema).parse(await res.json());
};

export const getShowsByEventForUser = async (eventId: string, userId?: string): Promise<ShowDto[]> => {
    const res = await json('api/show/by-event', { userId, eventId });
    if (res.status === 404) return [];
    if (!res.ok) throw new Error(`Failed to fetch shows: ${res.status}`);
    return z.array(ShowDtoSchema).parse(await res.json());
};
