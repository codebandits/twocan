import {useFetchGet} from '../fetch/fetch-get'

export type Bird = {
    id: string
    firstName: string
    lastName: string
    lastFlight: string | null
}

export const useBirds = () => useFetchGet<Bird[]>('/api/birds', 'birds')
