import {useDrawerToggle} from "./drawer";
import {AppBar, Box, IconButton, Toolbar, Typography} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import React from "react";

type Props = {
    title: string
    children?: React.ReactNode
}

export const Page = ({title, children}: Props) => {
    const drawerToggle = useDrawerToggle()
    return (
        <Box>
            <AppBar position="fixed" sx={{zIndex: (theme) => theme.zIndex.drawer + 1}}>
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        edge="start"
                        onClick={drawerToggle}
                        sx={{mr: 2, display: {sm: 'none'}}}>
                        <MenuIcon/>
                    </IconButton>
                    <Typography variant="h6" noWrap component="div">{title}</Typography>
                </Toolbar>
            </AppBar>
            <Box component="main">
                <Toolbar/>
                {children}
            </Box>
        </Box>
    );
}