import React from "react";
import {createMuiTheme, CssBaseline, MuiThemeProvider, useMediaQuery} from "@material-ui/core";
import {indigo, lightBlue, lightGreen, orange, red, yellow} from "@material-ui/core/colors";

type Props = {
    children?: React.ReactNode
}

export const ThemeProvider = ({children}: Props) => {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)')

    const theme = React.useMemo(() => createMuiTheme({
        palette: {
            type: prefersDarkMode ? 'dark' : 'light',
            primary: {main: lightBlue[700]},
            secondary: {main: orange[600]},
            warning: {main: yellow[500]},
            info: {main: indigo[500]},
            error: {main: red[500]},
            success: {main: lightGreen[500]},
        },
    }), [prefersDarkMode])

    return (
        <MuiThemeProvider theme={theme}>
            <CssBaseline/>
            {children}
        </MuiThemeProvider>
    )
}
