import useSWR from 'swr'

export type Session = {
    userId: string
    name: string
}

export const useSession = () => {
    return useSWR<Session | null>('/api/session', fetcher)
}

const fetcher = async <T>(url: string): Promise<T> => {
    const response = await fetch(url)
    const json = await response.json()
    return json.data
}
