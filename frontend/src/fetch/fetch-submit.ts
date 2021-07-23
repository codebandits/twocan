import {useCallback, useRef} from "react";
import {
    Request,
    ResponseAccepted,
    ResponseBadRequestErrors,
    ResponseBadRequestMessage,
    ResponseCreated,
    ResponseForbidden,
    ResponseInternalServerError,
    ResponseUnauthorized
} from "./fetch";
import {QueryKey} from "react-query/types/core/types";
import {useQueryClient} from "react-query";

export type FetchSubmitResponse<RequestType extends Request | void> =
    | ResponseAccepted
    | ResponseCreated
    | ResponseBadRequestErrors<RequestType extends Request ? RequestType : never>
    | ResponseBadRequestMessage
    | ResponseUnauthorized
    | ResponseForbidden
    | ResponseInternalServerError

type FetchSubmitOptions = {
    invalidateQueries?: QueryKey[]
}

export const useFetchSubmit = <RequestType extends Request | void>(url: string, options?: FetchSubmitOptions) => {
    const queryClient = useQueryClient()
    const optionsRef = useRef(options)

    return useCallback(async (data: RequestType): Promise<FetchSubmitResponse<RequestType>> => {
        const fetchResponse = await fetch(url, {method: 'post', body: JSON.stringify(data)})
        const response: FetchSubmitResponse<RequestType> = await fetchResponse.json()
        if (optionsRef.current && optionsRef.current.invalidateQueries) {
            if (isFetchSubmitResponseSuccess(response)) {
                const invalidations = optionsRef.current.invalidateQueries.map(queryKey => queryClient.invalidateQueries(queryKey))
                await Promise.all(invalidations)
            }
        }
        return response
    }, [queryClient, url])
}

const isFetchSubmitResponseAccepted = <RequestType extends Request | void>(response: FetchSubmitResponse<RequestType>): response is ResponseAccepted => {
    return response.status === 'ACCEPTED'
}

const isFetchSubmitResponseCreated = <RequestType extends Request | void>(response: FetchSubmitResponse<RequestType>): response is ResponseCreated => {
    return response.status === 'CREATED'
}

const isFetchSubmitResponseSuccess = <RequestType extends Request | void>(response: FetchSubmitResponse<RequestType>): response is ResponseAccepted | ResponseCreated => {
    return isFetchSubmitResponseAccepted(response) || isFetchSubmitResponseCreated(response)
}
