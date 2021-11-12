import React from 'react'
import {ThemeProvider} from '@mui/material'
import {BrowserRouter} from 'react-router-dom'
import {theme} from './layout/theme'
import {DrawerProvider} from './layout/drawer'
import {AppRoutes} from './routes'

export const App = () => (
  <Providers>
    <AppRoutes/>
  </Providers>
)

const Providers: React.FC = ({children}) => (
  <BrowserRouter>
    <DrawerProvider>
      <ThemeProvider theme={theme}>
        {children}
      </ThemeProvider>
    </DrawerProvider>
  </BrowserRouter>
)
