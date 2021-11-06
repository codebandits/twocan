import {useFetchSubmit} from "../fetch/fetch-submit";

export const useLogout = () => useFetchSubmit<void>('/api/logout', {invalidateQueries: ['session']})
