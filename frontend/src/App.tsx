import React, {useMemo} from 'react'
import {ThemeProvider} from './theme'
import {LoginPage} from './identity/login-page'
import {useSession} from './identity/session'
import {AppPage} from './app-page'
import {QueryClient, QueryClientProvider} from 'react-query'
import {BrowserRouter, Redirect, Route, Switch} from 'react-router-dom'
import {HomePage} from './home-page'

export const App = () => {
    const queryClient = useMemo(() => new QueryClient(), [])
    return (
        <ThemeProvider>
            <QueryClientProvider client={queryClient}>
                <BrowserRouter>
                    <Routes/>
                </BrowserRouter>
            </QueryClientProvider>
        </ThemeProvider>
    )
}

const Routes = () => {
    const {data: session} = useSession()
    if (session === undefined) {
        return null
    } else if (session) {
        return (
            <Switch>
                <Route path="/app" component={AppPage}/>
                <Redirect to="/app"/>
            </Switch>
        )
    } else {
        return (
            <Switch>
                <Route path="/login" component={LoginPage}/>
                <Route exact path="/" component={HomePage}/>
                <Redirect to="/"/>
            </Switch>
        )
    }
}
