import Divider from '@material-ui/core/Divider'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import ListItemIcon from '@material-ui/core/ListItemIcon'
import ListItemText from '@material-ui/core/ListItemText'
import React from 'react'
import {useIsLoggedIn} from '../identity/is-logged-in'
import {Link} from 'react-router-dom'
import {Home} from '@material-ui/icons'
import {Box, SvgIcon} from '@material-ui/core'
import {ReactComponent as LoginIcon} from './login.svg'
import {ReactComponent as LogoutIcon} from './logout.svg'
import {useLogout} from '../identity/logout'
import {ReactComponent as BirdIcon} from '../birds/bird.svg'
import {ReactComponent as FlockIcon} from '../birds/flock.svg'
import {ReactComponent as FlightIcon} from '../birds/flight.svg'

export const DrawerMenu = () => {
    const loggedIn = useIsLoggedIn()
    const logout = useLogout()
    return (
        <div data-testid="drawer-menu">
            {loggedIn && (
                <>
                    <List>
                        <ListItem button component={Link} to="/birds">
                            <ListItemIcon>
                                <SvgIcon><BirdIcon/></SvgIcon>
                            </ListItemIcon>
                            <ListItemText primary="Birds"/>
                        </ListItem>
                        <Box display="none">
                            <ListItem button component={Link} to="/flocks">
                                <ListItemIcon>
                                    <SvgIcon><FlockIcon/></SvgIcon>
                                </ListItemIcon>
                                <ListItemText primary="Flocks"/>
                            </ListItem>
                            <ListItem button component={Link} to="/flights">
                                <ListItemIcon>
                                    <SvgIcon><FlightIcon/></SvgIcon>
                                </ListItemIcon>
                                <ListItemText primary="Flights"/>
                            </ListItem>
                        </Box>
                    </List>
                    <Divider/>
                    <List>
                        <ListItem button onClick={() => logout()}>
                            <ListItemIcon><SvgIcon><LogoutIcon/></SvgIcon></ListItemIcon>
                            <ListItemText primary="Logout"/>
                        </ListItem>
                    </List>
                </>
            )}

            {!loggedIn && (
                <>
                    <List>
                        <ListItem button component={Link} to="/">
                            <ListItemIcon><Home/></ListItemIcon>
                            <ListItemText primary="Home"/>
                        </ListItem>
                        <ListItem button component={Link} to="/login">
                            <ListItemIcon><SvgIcon><LoginIcon/></SvgIcon></ListItemIcon>
                            <ListItemText primary="Login"/>
                        </ListItem>
                    </List>
                </>
            )}
        </div>
    )
}
