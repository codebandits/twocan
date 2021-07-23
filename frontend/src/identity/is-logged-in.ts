import {isFetchGetResponseOk} from "../fetch/fetch-get";
import {useSession} from "./session";

export const useIsLoggedIn = () => {
    const {data: sessionResponse} = useSession()
    return sessionResponse !== undefined && isFetchGetResponseOk(sessionResponse) && sessionResponse.data !== null
}
