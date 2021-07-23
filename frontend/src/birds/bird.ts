import {useFetchGet} from '../fetch/fetch-get'

export type Bird = {
    id: string
    firstName: string
    lastName: string
}

export const useBirds = () => useFetchGet<Bird[]>('/api/birds', 'birds')
