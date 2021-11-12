import {ReactComponent as LogoGraphic} from './logo.svg'
import {useTheme} from '@mui/material'

export const Logo = () => {
  const theme = useTheme()
  return <LogoGraphic fill={theme.palette.primary.main}/>
}
