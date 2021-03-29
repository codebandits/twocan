import React from 'react'
import {ThemeProvider} from './theme'
import {LoginPage} from './identity/login-page'
import {useSession} from './identity/session'
import {BirdsPage} from './birds/birds-page'
import {QueryClient, QueryClientProvider} from 'react-query'
import {BrowserRouter, Redirect, Route, Switch} from 'react-router-dom'
import {HomePage} from './home-page'

const queryClient = new QueryClient()

export const App = () => {
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
                <Route path="/birds" component={BirdsPage}/>
                <Redirect to="/birds"/>
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
