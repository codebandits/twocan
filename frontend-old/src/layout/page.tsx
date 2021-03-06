import React, {useState} from 'react'
import AppBar from '@material-ui/core/AppBar'
import CssBaseline from '@material-ui/core/CssBaseline'
import Drawer from '@material-ui/core/Drawer'
import Hidden from '@material-ui/core/Hidden'
import IconButton from '@material-ui/core/IconButton'
import MenuIcon from '@material-ui/icons/Menu'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles'
import {DrawerMenu} from './drawer-menu'

const drawerWidth = 240

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        root: {
            display: 'flex',
        },
        drawer: {
            [theme.breakpoints.up('md')]: {
                width: drawerWidth,
                flexShrink: 0,
            },
        },
        appBar: {
            zIndex: theme.zIndex.drawer + 1,
        },
        menuButton: {
            marginRight: theme.spacing(2),
            [theme.breakpoints.up('md')]: {
                display: 'none',
            },
        },
        toolbarOffset: theme.mixins.toolbar,
        drawerPaper: {
            width: drawerWidth,
        },
        content: {
            flexGrow: 1,
        },
    }),
)

interface Props {
    title: string
    children?: React.ReactNode
}

export const Page = ({title, children}: Props) => {
    const classes = useStyles()
    const [mobileOpen, setMobileOpen] = useState(false)

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen)
    }

    return (
        <div className={classes.root}>
            <CssBaseline/>
            <AppBar position="fixed" className={classes.appBar}>
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        edge="start"
                        onClick={handleDrawerToggle}
                        className={classes.menuButton}>
                        <MenuIcon/>
                    </IconButton>
                    <Typography variant="h6" noWrap>{title}</Typography>
                </Toolbar>
            </AppBar>
            <nav className={classes.drawer}>
                <Hidden mdUp>
                    <Drawer
                        variant="temporary"
                        open={mobileOpen}
                        onClose={handleDrawerToggle}
                        classes={{paper: classes.drawerPaper}}
                        ModalProps={{keepMounted: true}}>
                        <DrawerMenu/>
                    </Drawer>
                </Hidden>
                <Hidden smDown>
                    <Drawer
                        classes={{paper: classes.drawerPaper}}
                        variant="permanent"
                        open>
                        <div className={classes.toolbarOffset}/>
                        <DrawerMenu/>
                    </Drawer>
                </Hidden>
            </nav>
            <main className={classes.content}>
                <div className={classes.toolbarOffset}/>
                {children}
            </main>
        </div>
    )
}
