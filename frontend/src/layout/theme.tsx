import {Link as RouterLink, LinkProps as RouterLinkProps} from "react-router-dom";
import React from "react";
import {createTheme, LinkProps} from "@mui/material";

type LinkComponentProps = Omit<RouterLinkProps, 'to'> & { href: RouterLinkProps['to'] }

export const LinkComponent = React.forwardRef<any, LinkComponentProps>((props, ref) => {
    const {href, ...other} = props
    return <RouterLink ref={ref} to={href} {...other} />
});

export const theme = createTheme({
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
