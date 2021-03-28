import React, {useMemo} from 'react'
import {ThemeProvider} from './theme'
import {LoginPage} from './identity/login-page'
import {useSession} from './identity/session'
import {SamplePage} from './sample'
import {QueryClient, QueryClientProvider} from 'react-query'

export const App = () => {
    const queryClient = useMemo(() => new QueryClient(), [])
    return (
        <ThemeProvider>
            <QueryClientProvider client={queryClient}>
                <Routes/>
            </QueryClientProvider>
        </ThemeProvider>
    )
}

const Routes = () => {
    const {data: session} = useSession()
    if (session === undefined) {
        return null
    }
    return session ? <SamplePage/> : <LoginPage/>
}
