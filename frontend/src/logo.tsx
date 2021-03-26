import {ReactComponent as LogoGraphic} from "./logo.svg"
import {useTheme} from "@material-ui/core/styles"

export const Logo = () => {
    const theme = useTheme()
    const fill = theme.palette.type === 'light' ? theme.palette.common.black : theme.palette.common.white
    return <LogoGraphic fill={fill}/>
}
