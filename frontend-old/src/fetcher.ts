export const fetcher = async <T>(url: string): Promise<T> => {
    const response = await fetch(url)
    const json = await response.json()
    return json.data
}
