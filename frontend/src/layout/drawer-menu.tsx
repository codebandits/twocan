import Divider from "@material-ui/core/Divider";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import InboxIcon from "@material-ui/icons/MoveToInbox";
import MailIcon from "@material-ui/icons/Mail";
import ListItemText from "@material-ui/core/ListItemText";
import React from "react";
import {useSession} from "../identity/session";
import {Link} from "react-router-dom";
import {Home} from "@material-ui/icons";
import {SvgIcon} from "@material-ui/core";
import {ReactComponent as LoginIcon} from './login.svg'
import {ReactComponent as LogoutIcon} from './logout.svg'
import {useLogout} from '../identity/logout'

export const DrawerMenu = () => {
    const {data: session} = useSession()
    const loggedIn = Boolean(session)
    const logout = useLogout()
    return (
        <div data-testid="drawer-menu">
            {loggedIn && (
                <>
                    <List>
                        {['Inbox', 'Starred', 'Send email', 'Drafts'].map((text, index) => (
                            <ListItem button key={text}>
                                <ListItemIcon>{index % 2 === 0 ? <InboxIcon/> : <MailIcon/>}</ListItemIcon>
                                <ListItemText primary={text}/>
                            </ListItem>
                        ))}
                    </List>
                    <Divider/>
                    <List>
                        {['All mail', 'Trash', 'Spam'].map((text, index) => (
                            <ListItem button key={text}>
                                <ListItemIcon>{index % 2 === 0 ? <InboxIcon/> : <MailIcon/>}</ListItemIcon>
                                <ListItemText primary={text}/>
                            </ListItem>
                        ))}
                    </List>
                    <Divider/>
                    <List>
                        <ListItem button onClick={logout}>
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
    );
}
