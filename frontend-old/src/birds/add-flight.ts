import {useFetchSubmit} from '../fetch/fetch-submit'

type Request = {
    birdId: string
}

export const useAddFlight = () => useFetchSubmit<Request>('/api/flights', {invalidateQueries: ['birds']})
