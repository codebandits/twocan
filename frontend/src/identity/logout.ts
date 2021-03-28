import {useEffect, useState} from "react";
import {useQueryClient} from "react-query";
import {makeCancelable} from "../promise";

export const useLogout = () => {
    const [submitting, setSubmitting] = useState(false)
    const queryClient = useQueryClient()
    useEffect(() => {
        if (submitting) {
            const cancelable = makeCancelable(fetch('/api/logout', {method: 'post'}))
            cancelable.promise
                .then(() => queryClient.invalidateQueries('session'))
                .then(() => setSubmitting(false))
                .catch(() => setSubmitting(false))
            return () => {
                cancelable.cancel()
                setSubmitting(false)
            }
        }

    }, [queryClient, submitting])
    return () => setSubmitting(true)
}
