import {useQueryClient} from "react-query";
import {useCallback} from "react";

type Request = {
    firstName: string
    lastName: string
}

export const useAddBird = () => {
    const queryClient = useQueryClient()
    return useCallback((data: Request) =>
        fetch('/api/birds', {
            method: 'post',
            body: JSON.stringify(data)
        }).then(() => queryClient.invalidateQueries('birds')), [queryClient])
}
