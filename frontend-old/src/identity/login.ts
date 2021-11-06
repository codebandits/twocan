import {useFetchSubmit} from "../fetch/fetch-submit";

export type LoginData = {
    emailAddress: string
}

export const useLogin = () => useFetchSubmit<LoginData>('/api/login', {invalidateQueries: ['session']})
