<script lang="ts">
	import "$lib/styles/globals.css";

	import favicon from "$lib/assets/favicon.svg";
	import { onMount } from "svelte";
	import { invalidateAll } from "$app/navigation";
	import {
		SessionStore,
		SupaStore,
		UserStore,
	} from "$lib/stores/SupaStore.js";

	let { data, children } = $props();
	let { supabase, session, user } = $derived(data);

	onMount(() => {
		const {
			data: { subscription },
		} = supabase.auth.onAuthStateChange((event, newSession) => {
			if (
				newSession?.expires_at !== session?.expires_at ||
				event === "SIGNED_OUT"
			) {
				invalidateAll();
			}
		});

		SupaStore.set(supabase);
		UserStore.set(user);
		SessionStore.set(session);

		return () => subscription.unsubscribe();
	});
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

{@render children()}
