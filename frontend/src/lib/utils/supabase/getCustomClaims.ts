import type { CustomClaimsT } from "$lib/types/customClaims.type";
import type { User } from "@supabase/supabase-js";

const getCustomClaims = (user: User | null): CustomClaimsT | null => {
    if (!user) return null;
    return user.app_metadata.custom_claims;
}

export default getCustomClaims;