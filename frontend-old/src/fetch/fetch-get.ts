import {
    ResponseForbidden,
    ResponseInternalServerError,
    ResponseNotFound,
    ResponseOk,
    ResponseUnauthorized
} from "./fetch";
import {QueryKey} from "react-query/types/core/types";
import {UseQueryResult} from "react-query/types/react/types";
import {useCallback} from "react";
import {useQuery} from "react-query";

export type FetchGetResponse<DataType> =
    | ResponseOk<DataType>
    | ResponseNotFound
    | ResponseUnauthorized
    | ResponseForbidden
    | ResponseInternalServerError

export const useFetchGet = <DataType>(url: string, queryKey: QueryKey): UseQueryResult<FetchGetResponse<DataType>> => {
    const fetcher = useCallback(async (): Promise<FetchGetResponse<DataType>> => {
        const response = await fetch(url, {method: 'get'})
        return await response.json()
    }, [url])
    return useQuery(queryKey, fetcher)
}

export const isFetchGetResponseOk = <DataType>(response: FetchGetResponse<DataType>): response is ResponseOk<DataType> => {
    return response.status === 'OK'
}
