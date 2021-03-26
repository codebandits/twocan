import {createStyles, makeStyles} from "@material-ui/core/styles";
import React from "react";

const useStyles = makeStyles(theme =>
    createStyles({
        root: {
            padding: theme.spacing(3),
        },
    }),
)

type Props = {
    children?: React.ReactNode
}

export const PageContent = ({children}: Props) => {
    const classes = useStyles()
    return (
        <div className={classes.root}>
            {children}
        </div>
    )
}
