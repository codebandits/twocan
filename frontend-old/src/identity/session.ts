import {User} from './user'
import {useFetchGet} from '../fetch/fetch-get'

export type Session = {
    id: string
    user: User
}

export const useSession = () => useFetchGet<Session | null>('/api/session', 'session')
