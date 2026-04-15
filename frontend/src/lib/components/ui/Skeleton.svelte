<script lang="ts">
    interface Props {
        height?: string;
        width?: string;
        class?: string;
    }
    let { height = 'h-4', width = 'w-full', class: className = '' }: Props = $props();
</script>

<div class="skeleton-shimmer {height} {width} {className}"></div>
