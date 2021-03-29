import {useQuery} from 'react-query'
import {fetcher} from '../fetcher'
import {User} from './user'

export type Session = {
    id: string
    user: User
}

export const useSession = () => useQuery('session', () => fetcher<Session | null>('/api/session'))
