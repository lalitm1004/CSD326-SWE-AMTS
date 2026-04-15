<script lang="ts">
    interface Props {
        label?: string;
        placeholder?: string;
        value?: string;
        error?: string;
        disabled?: boolean;
        required?: boolean;
        rows?: number;
        id?: string;
        name?: string;
        class?: string;
    }

    let {
        label,
        placeholder = '',
        value = $bindable(''),
        error,
        disabled = false,
        required = false,
        rows = 4,
        id,
        name,
        class: className = ''
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
    <textarea
        id={inputId}
        {name}
        {placeholder}
        {disabled}
        {required}
        {rows}
        bind:value
        class="
            w-full px-3 py-2 text-sm resize-y
            bg-[var(--color-raised)] text-[var(--color-text)]
            border border-[var(--color-border)]
            focus:outline-none focus:border-[var(--color-gold)]
            placeholder:text-[var(--color-text-muted)]
            disabled:opacity-40 disabled:cursor-not-allowed
            transition-colors duration-150
            {error ? 'border-[var(--color-error)]' : ''}
        "
    ></textarea>
    {#if error}
        <span class="text-xs text-[var(--color-error)]">{error}</span>
    {/if}
</div>
