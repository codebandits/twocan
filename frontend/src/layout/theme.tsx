import {Link as RouterLink, LinkProps as RouterLinkProps} from "react-router-dom";
import React from "react";
import {createTheme, LinkProps} from "@mui/material";
import {indigo, lightBlue, lightGreen, orange, red, yellow} from "@mui/material/colors";

type LinkComponentProps = Omit<RouterLinkProps, 'to'> & { href: RouterLinkProps['to'] }

export const LinkComponent = React.forwardRef<any, LinkComponentProps>((props, ref) => {
    const {href, ...other} = props
    return <RouterLink ref={ref} to={href} {...other} />
});

export const theme = createTheme({
    palette: {
        primary: {main: lightBlue[700]},
        secondary: {main: orange[600]},
        warning: {main: yellow[500]},
        info: {main: indigo[500]},
        error: {main: red[500]},
        success: {main: lightGreen[500]},
    },
    components: {
        MuiLink: {
            defaultProps: {
                component: LinkComponent,
            } as LinkProps,
        },
        MuiButtonBase: {
            defaultProps: {
                LinkComponent: LinkComponent,
            },
        },
    },
})
