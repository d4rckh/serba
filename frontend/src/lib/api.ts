import {QueryClient} from "@tanstack/react-query";
import type {paths} from "./v1";
import createFetchClient from "openapi-fetch";
import createClient from "openapi-react-query";

const fetchWithCookies: typeof fetch = (input, init = {}) =>
    fetch(input, {
        ...init,
        credentials: "include",
    });

const baseUrl = import.meta.env.PROD ? "/" : "/api";

const fetchClient = createFetchClient<paths>({
    baseUrl,
    fetch: fetchWithCookies,
});

export const $api = createClient(fetchClient);
export const queryClient = new QueryClient();