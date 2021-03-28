import {MouseEventHandler, useEffect, useState} from 'react'
import {AccountCircle} from '@material-ui/icons'
import {IconButton, Menu, MenuItem} from '@material-ui/core'
import {useSession} from './session'
import {useQueryClient} from 'react-query'
import {makeCancelable} from '../promise'

const useLogout = () => {
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

export const SessionMenu = () => {
    const {data: session} = useSession()
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)
    const logout = useLogout()
    const handleClick: MouseEventHandler<HTMLButtonElement> = (event) => {
        setAnchorEl(event.currentTarget)
    }
    const handleClose = () => {
        setAnchorEl(null)
    }
    const handleLogout = () => {
        handleClose()
        logout()
    }

    if (!session) {
        return null
    }
    return (
        <>
            <IconButton
                onClick={handleClick}
                color="inherit"
                data-testid="session-menu-button">
                <AccountCircle/>
            </IconButton>
            <Menu
                data-testid="session-menu"
                anchorOrigin={{vertical: 'bottom', horizontal: 'right'}}
                transformOrigin={{vertical: 'top', horizontal: 'right'}}
                anchorEl={anchorEl}
                getContentAnchorEl={null}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={handleClose}>
                <MenuItem onClick={handleLogout}>Logout</MenuItem>
            </Menu>
        </>
    )
}
