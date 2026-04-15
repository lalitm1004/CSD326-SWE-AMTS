import { env } from "$env/dynamic/public";
import { get } from "svelte/store";
import { SessionStore } from "$lib/stores/SupaStore";

interface FetchOptions extends RequestInit {
    headers?: Record<string, string>;
}

/**
 * Performs an authenticated fetch request to the backend.
 *
 * This function automatically adds a Bearer token from the Svelte store
 * to the Authorization header and ensures the backend URL and route are properly joined
 * without duplicate slashes.
 *
 * @param route - The API route (e.g., "api/team" or "/api/team").
 * @param options - Optional fetch configuration (headers, method, body, etc.).
 * @returns A Promise resolving to the Fetch API Response object.
 */
export const fetchWithAuth = async (route: string, options: FetchOptions = {}): Promise<Response> => {
    const session = get(SessionStore);
    const cleanedUrl = (env.PUBLIC_BACKEND_URL ?? "").replace(/\/+$/, "");
    const cleanedRoute = route.replace(/^\/+/, "");
    const headers: Record<string, string> = { ...options.headers };

    if (session?.access_token) {
        headers.Authorization = `Bearer ${session.access_token}`;
    }

    return fetch(`${cleanedUrl}/${cleanedRoute}`, {
        ...options,
        headers
    });
}
