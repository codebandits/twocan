import {AccountCircle} from '@material-ui/icons'
import {IconButton} from '@material-ui/core'
import {useSession} from './session'

export const SessionMenu = () => {
    const {data: session} = useSession()
    if (!session) {
        return null
    }
    return (
        <IconButton color="inherit" data-testid="session-menu">
            <AccountCircle/>
        </IconButton>
    )
}
