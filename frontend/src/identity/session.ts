import {useQuery} from 'react-query'

export type Session = {
    userId: string
    name: string
}

export const useSession = () => useQuery('session', () => fetcher<Session | null>('/api/session'))

const fetcher = async <T>(url: string): Promise<T> => {
    const response = await fetch(url)
    const json = await response.json()
    return json.data
}
