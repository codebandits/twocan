import React, {createContext, useContext, useState} from "react"
import {Box, Divider, Drawer, Hidden, List, ListItemButton, ListItemIcon, Toolbar} from "@mui/material";
import InboxIcon from "@mui/icons-material/MoveToInbox";
import MailIcon from "@mui/icons-material/Mail";
import HomeIcon from "@mui/icons-material/Home";
import ListItemText from "@mui/material/ListItemText";
import {LinkComponent} from "./theme";
import {Outlet} from "react-router-dom";

const drawerWidth = 240

export const DrawerLayout = () => (
    <Box sx={{display: 'flex'}}>
        <ResponsiveDrawer/>
        <Box component="main" sx={{flexGrow: 1}}>
            <Outlet/>
        </Box>
    </Box>
)

const ResponsiveDrawer = () => {
    const drawerOpen = useDrawerOpen()
    const drawerToggle = useDrawerToggle()
    return (
        <>
            <Hidden mdUp>
                <Drawer
                    variant="temporary"
                    open={drawerOpen}
                    onClose={drawerToggle}
                    onClick={drawerToggle}
                    ModalProps={{keepMounted: true}}
                    sx={{'& .MuiDrawer-paper': {boxSizing: 'border-box', width: drawerWidth}}}>
                    <DrawerList/>
                </Drawer>
            </Hidden>
            <Hidden smDown>
                <Drawer
                    variant="permanent"
                    anchor="left"
                    open
                    sx={{
                        width: drawerWidth,
                        flexShrink: 0,
                        '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
                    }}>
                    <DrawerList/>
                </Drawer>
            </Hidden>
        </>
    );
}

// TODO: simplify when resolved https://git.io/JXtUb
const DrawerList = () => (
    <>
        <Toolbar/>
        <Divider/>
        <List>
            <ListItemButton href="/" component={LinkComponent}>
                <ListItemIcon>
                    <HomeIcon/>
                </ListItemIcon>
                <ListItemText primary="Home"/>
            </ListItemButton>
        </List>
        <Divider/>
        <List>
            <ListItemButton href="/mail" component={LinkComponent}>
                <ListItemIcon>
                    <MailIcon/>
                </ListItemIcon>
                <ListItemText primary="Mail"/>
            </ListItemButton>
            <ListItemButton href="/inbox" component={LinkComponent}>
                <ListItemIcon>
                    <InboxIcon/>
                </ListItemIcon>
                <ListItemText primary="Inbox"/>
            </ListItemButton>
        </List>
    </>
)

type Context = {
    open: boolean
    toggle: () => void
}

const DrawerContext = createContext<Context | null>(null)

export const DrawerProvider: React.FC = ({children}) => {
    const [open, setOpen] = useState(false)
    const toggle = () => setOpen(value => !value)
    return (
        <DrawerContext.Provider value={{open, toggle}}>
            {children}
        </DrawerContext.Provider>
    )
}

export const useDrawerOpen = () => {
    const context = useContext(DrawerContext)
    if (context === null) {
        throw new Error('useDrawerOpen must be used within its context provider')
    }
    return context.open
}

export const useDrawerToggle = () => {
    const context = useContext(DrawerContext)
    if (context === null) {
        throw new Error('useDrawerToggle must be used within its context provider')
    }
    return context.toggle
}
