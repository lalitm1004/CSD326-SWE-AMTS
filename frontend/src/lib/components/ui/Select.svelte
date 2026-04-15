<script lang="ts">
    interface Option {
        value: string;
        label: string;
    }

    interface Props {
        label?: string;
        options: Option[];
        value?: string;
        error?: string;
        disabled?: boolean;
        required?: boolean;
        placeholder?: string;
        id?: string;
        name?: string;
        class?: string;
        onchange?: (e: Event) => void;
    }

    let {
        label,
        options,
        value = $bindable(''),
        error,
        disabled = false,
        required = false,
        placeholder = 'Select…',
        id,
        name,
        class: className = '',
        onchange
    }: Props = $props();

    const generatedId = crypto.randomUUID();
    const inputId = $derived(id ?? generatedId);
</script>

<div class="flex flex-col gap-1.5 {className}">
    {#if label}
        <label for={inputId} class="text-xs font-medium text-[var(--color-text-muted)] uppercase tracking-widest">
            {label}{required ? ' *' : ''}
        </label>
    {/if}
    <select
        id={inputId}
        {name}
        {disabled}
        {required}
        bind:value
        {onchange}
        class="
            w-full px-3 py-2 text-sm
            bg-[var(--color-raised)] text-[var(--color-text)]
            border border-[var(--color-border)]
            focus:outline-none focus:border-[var(--color-gold)]
            disabled:opacity-40 disabled:cursor-not-allowed
            transition-colors duration-150 cursor-pointer
            {error ? 'border-[var(--color-error)]' : ''}
        "
    >
        <option value="" disabled>{placeholder}</option>
        {#each options as opt}
            <option value={opt.value}>{opt.label}</option>
        {/each}
    </select>
    {#if error}
        <span class="text-xs text-[var(--color-error)]">{error}</span>
    {/if}
</div>
