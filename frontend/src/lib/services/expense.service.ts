import { fetchWithAuth } from '$lib/utils/fetchWithAuth';
import { z } from 'zod';
import { ExpenseDtoSchema, BalanceSheetDtoSchema, ConsolidatedYearlyDtoSchema, type ExpenseDto, type BalanceSheetDto, type ConsolidatedYearlyDto } from '$lib/types/api/expense.types';

const jsonPost = (route: string, body: unknown) =>
    fetchWithAuth(route, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

export const createExpense = async (data: {
    clerkUserId: string;
    showId: string;
    name: string;
    description?: string;
    amount: number;
}): Promise<void> => {
    const res = await jsonPost('api/expense', data);
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.error ?? `Failed to create expense: ${res.status}`);
    }
};

export const deleteExpense = async (clerkUserId: string, expenseId: string): Promise<void> => {
    const res = await fetchWithAuth(`api/expense?clerkUserId=${clerkUserId}&expenseId=${expenseId}`, {
        method: 'DELETE'
    });
    if (!res.ok) throw new Error(`Failed to delete expense: ${res.status}`);
};

export const updateExpenseName = async (expenseId: string, clerkUserId: string, name: string): Promise<void> => {
    const res = await fetchWithAuth(`api/expense/name?expenseId=${expenseId}&clerkUserId=${clerkUserId}&newName=${encodeURIComponent(name)}`, {
        method: 'PATCH'
    });
    if (!res.ok) throw new Error(`Failed to update expense: ${res.status}`);
};

export const updateExpenseDescription = async (expenseId: string, clerkUserId: string, description: string): Promise<void> => {
    const res = await fetchWithAuth(`api/expense/description?expenseId=${expenseId}&clerkUserId=${clerkUserId}&newDescription=${encodeURIComponent(description)}`, {
        method: 'PATCH'
    });
    if (!res.ok) throw new Error(`Failed to update expense description: ${res.status}`);
};

export const updateExpenseAmount = async (expenseId: string, clerkUserId: string, amount: number): Promise<void> => {
    const res = await fetchWithAuth(`api/expense/amount?expenseId=${expenseId}&clerkUserId=${clerkUserId}&newAmount=${amount}`, {
        method: 'PATCH'
    });
    if (!res.ok) throw new Error(`Failed to update expense: ${res.status}`);
};

export const getBalanceSheet = async (userId: string, showId: string): Promise<BalanceSheetDto> => {
    const res = await fetchWithAuth(`api/expense/balance-sheet?userId=${userId}&showId=${showId}`);
    if (!res.ok) throw new Error(`Failed to fetch balance sheet: ${res.status}`);
    return BalanceSheetDtoSchema.parse(await res.json());
};

export const getBalanceSheetsByEvent = async (userId: string, eventId: string): Promise<BalanceSheetDto[]> => {
    const res = await fetchWithAuth(`api/expense/balance-sheet/by-event?userId=${userId}&eventId=${eventId}`);
    if (!res.ok) throw new Error(`Failed to fetch balance sheets: ${res.status}`);
    return z.array(BalanceSheetDtoSchema).parse(await res.json());
};

export const getBalanceSheetsByYear = async (userId: string, year: number): Promise<BalanceSheetDto[]> => {
    const res = await fetchWithAuth(`api/expense/balance-sheet/by-year?userId=${userId}&year=${year}`);
    if (!res.ok) throw new Error(`Failed to fetch balance sheets: ${res.status}`);
    return z.array(BalanceSheetDtoSchema).parse(await res.json());
};

export const getYearlyConsolidated = async (userId: string, year: number): Promise<ConsolidatedYearlyDto> => {
    const res = await fetchWithAuth(`api/expense/balance-sheet/yearly-consolidated?userId=${userId}&year=${year}`);
    if (!res.ok) throw new Error(`Failed to fetch yearly balance sheet: ${res.status}`);
    return ConsolidatedYearlyDtoSchema.parse(await res.json());
};
