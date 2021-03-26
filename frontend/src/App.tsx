import React from 'react'
import {ThemeProvider} from "./theme"
import {LoginPage} from "./identity/login-page";
import {useSession} from "./identity/session";
import {SamplePage} from "./sample";

export const App = () => {
    const {data: session} = useSession()

    if (session === undefined) {
        return null
    }

    return (
        <ThemeProvider>
            {session ? <SamplePage/> : <LoginPage/>}
        </ThemeProvider>
    )
}
