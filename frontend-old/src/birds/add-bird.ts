import {useFetchSubmit} from '../fetch/fetch-submit'

type Request = {
    firstName: string
    lastName: string
}

export const useAddBird = () => useFetchSubmit<Request>('/api/birds', {invalidateQueries: ['birds']})
