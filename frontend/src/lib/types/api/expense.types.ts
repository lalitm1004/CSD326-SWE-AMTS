import { z } from 'zod';

const MoneyLikeSchema = z.union([
    z.number(),
    z.null(),
    z.object({
        amount: z.number()
    })
]).transform((value) => {
    if (value === null) return null;
    return typeof value === 'number' ? value : value.amount;
});

export const ExpenseDtoSchema = z.object({
    id: z.string().uuid(),
    financialClerkUserId: z.string().uuid().nullable().optional(),
    balanceSheetId: z.string().uuid(),
    name: z.string(),
    description: z.string().nullable().optional(),
    amount: MoneyLikeSchema,
    createdAt: z.string(),
});
export type ExpenseDto = z.infer<typeof ExpenseDtoSchema>;

export const BalanceSheetDtoSchema = z.object({
    id: z.string().uuid(),
    financialClerkUserId: z.string().uuid().nullable().optional(),
    showId: z.string().uuid().nullable().optional(),
    expenses: z.array(ExpenseDtoSchema).optional().default([]),
    totalExpenses: MoneyLikeSchema.optional(),
    createdAt: z.string(),
});
export type BalanceSheetDto = z.infer<typeof BalanceSheetDtoSchema>;

export const ConsolidatedYearlyDtoSchema = z.object({
    id: z.string().uuid().optional(),
    year: z.number(),
    totalExpenses: MoneyLikeSchema,
    balanceSheets: z.array(BalanceSheetDtoSchema).optional(),
    expenses: z.array(ExpenseDtoSchema).optional(),
    numberOfShows: z.number().optional(),
    generatedAt: z.string().optional()
}).transform((data) => ({
    year: data.year,
    totalExpenses: data.totalExpenses ?? 0,
    balanceSheets: data.balanceSheets ?? [],
    id: data.id,
    expenses: data.expenses ?? [],
    numberOfShows: data.numberOfShows,
    generatedAt: data.generatedAt
}));
export type ConsolidatedYearlyDto = z.infer<typeof ConsolidatedYearlyDtoSchema>;
