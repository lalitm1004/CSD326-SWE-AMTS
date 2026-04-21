import { env } from '$env/dynamic/public';
import { z } from 'zod';
import { EventDtoSchema, ShowDtoSchema, type EventDto, type ShowDto } from '$lib/types/api/event.types';
import { UserDtoSchema, type UserDto } from '$lib/types/api/user.types';
import { BalanceSheetDtoSchema, ConsolidatedYearlyDtoSchema, type BalanceSheetDto, type ConsolidatedYearlyDto } from '$lib/types/api/expense.types';
import { SeatDtoSchema, type SeatDto } from '$lib/types/api/seat.types';

type Locals = App.Locals;

function buildHeaders(locals: Locals, init?: RequestInit): Headers {
    const headers = new Headers(init?.headers);
    if (!headers.has('Authorization') && locals.session?.access_token) {
        headers.set('Authorization', `Bearer ${locals.session.access_token}`);
    }
    return headers;
}

async function backendFetch(locals: Locals, path: string, init?: RequestInit): Promise<Response> {
    return fetch(`${env.PUBLIC_BACKEND_URL ?? ''}/${path}`, {
        ...init,
        headers: buildHeaders(locals, init)
    });
}

export async function backendJson<T>(locals: Locals, path: string, init?: RequestInit): Promise<T> {
    const res = await backendFetch(locals, path, init);
    if (!res.ok) {
        throw new Error(`Backend request failed: ${res.status}`);
    }

    return res.json() as Promise<T>;
}

export async function backendJsonOrNull<T>(locals: Locals, path: string, init?: RequestInit): Promise<T | null> {
    const res = await backendFetch(locals, path, init);
    if (res.status === 404) return null;
    if (!res.ok) {
        throw new Error(`Backend request failed: ${res.status}`);
    }

    return res.json() as Promise<T>;
}

export async function fetchAllEvents(locals: Locals, userId?: string): Promise<EventDto[]> {
    const events = await backendJson<unknown>(locals, 'api/event/all', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId })
    });
    return z.array(EventDtoSchema).parse(events);
}

export async function fetchShowsByEvent(locals: Locals, eventId: string, userId?: string): Promise<ShowDto[]> {
    const path = userId ? 'api/show/by-event' : 'api/show/public/by-event';
    const body = userId ? { userId, eventId } : { eventId };
    const shows = await backendJson<unknown>(locals, path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    return z.array(ShowDtoSchema).parse(shows);
}

export async function fetchShow(locals: Locals, showId: string, userId?: string): Promise<ShowDto | null> {
    const path = userId ? 'api/show/get' : 'api/show/public/get';
    const body = userId ? { userId, showId } : { showId };
    const show = await backendJsonOrNull<unknown>(locals, path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    return show ? ShowDtoSchema.parse(show) : null;
}

export async function fetchShowSeats(locals: Locals, showId: string, userId?: string): Promise<SeatDto[]> {
    const seats = await backendJson<unknown>(locals, 'api/show/seats', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, showId })
    });
    return z.array(SeatDtoSchema).parse(seats);
}

export async function fetchUser(locals: Locals, userId: string): Promise<UserDto | null> {
    const user = await backendJsonOrNull<unknown>(locals, `api/user?id=${userId}`);
    return user ? UserDtoSchema.parse(user) : null;
}

export async function fetchUsersByIds(locals: Locals, ids: string[]): Promise<UserDto[]> {
    const users = await Promise.all(ids.map((id) => fetchUser(locals, id)));
    return users.filter((user): user is UserDto => user !== null);
}

export async function fetchBalanceSheet(locals: Locals, userId: string, showId: string): Promise<BalanceSheetDto | null> {
    const sheet = await backendJsonOrNull<unknown>(locals, `api/expense/balance-sheet?userId=${userId}&showId=${showId}`);
    return sheet ? BalanceSheetDtoSchema.parse(sheet) : null;
}

export async function fetchBalanceSheetsByEvent(locals: Locals, userId: string, eventId: string): Promise<BalanceSheetDto[]> {
    const sheets = await backendJson<unknown>(locals, `api/expense/balance-sheet/by-event?userId=${userId}&eventId=${eventId}`);
    return z.array(BalanceSheetDtoSchema).parse(sheets);
}

export async function fetchYearlyConsolidated(locals: Locals, userId: string, year: number): Promise<ConsolidatedYearlyDto> {
    const yearly = await backendJson<unknown>(locals, `api/expense/balance-sheet/yearly-consolidated?userId=${userId}&year=${year}`);
    return ConsolidatedYearlyDtoSchema.parse(yearly);
}
