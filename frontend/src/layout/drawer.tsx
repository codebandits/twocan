import React, {createContext, useContext, useState} from 'react'
import {Box, Divider, Drawer, Hidden, List, ListItemButton, ListItemIcon, Toolbar} from '@mui/material'
import InboxIcon from '@mui/icons-material/MoveToInbox'
import MailIcon from '@mui/icons-material/Mail'
import HomeIcon from '@mui/icons-material/Home'
import ListItemText from '@mui/material/ListItemText'
import {LinkComponent} from './theme'
import {Outlet} from 'react-router-dom'

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
  const drawerOnClose = useDrawerOnClose()
  return (
    <>
      <Hidden mdUp>
        <Drawer
          variant="temporary"
          open={drawerOpen}
          onClose={drawerOnClose}
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
            '& .MuiDrawer-paper': {width: drawerWidth, boxSizing: 'border-box'},
          }}>
          <DrawerList/>
        </Drawer>
      </Hidden>
    </>
  )
}

// TODO: simplify when resolved https://git.io/JXtUb
const DrawerList = () => {
  const drawerOnClose = useDrawerOnClose()
  return (
    <>
      <Toolbar/>
      <Divider/>
      <List onClick={drawerOnClose}>
        <ListItemButton href="/" component={LinkComponent}>
          <ListItemIcon>
            <HomeIcon/>
          </ListItemIcon>
          <ListItemText primary="Home"/>
        </ListItemButton>
      </List>
      <Divider/>
      <List onClick={drawerOnClose}>
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
}

type Context = {
    open: boolean
    onToggle: () => void
    onClose: () => void
}

const DrawerContext = createContext<Context | null>(null)

export const DrawerProvider: React.FC = ({children}) => {
  const [open, setOpen] = useState(false)
  const onToggle = () => setOpen(value => !value)
  const onClose = () => setOpen(false)
  return (
    <DrawerContext.Provider value={{open, onToggle, onClose}}>
      {children}
    </DrawerContext.Provider>
  )
}

export const useDrawerOpen = () => {
  const context = useContext(DrawerContext)
  if (context === null) {
    return false
  }
  return context.open
}

export const useDrawerOnToggle = () => {
  const context = useContext(DrawerContext)
  if (context === null) {
    return () => undefined
  }
  return context.onToggle
}

export const useDrawerOnClose = () => {
  const context = useContext(DrawerContext)
  if (context === null) {
    return () => undefined
  }
  return context.onClose
}
