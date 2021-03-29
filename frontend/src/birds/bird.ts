import {useQuery} from "react-query";
import {fetcher} from "../fetcher";

export type Bird = {
    id: string
    firstName: string
    lastName: string
}

export const useBirds = () => useQuery('birds', () => fetcher<Bird[]>('/api/birds'))
