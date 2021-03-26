import React from 'react'
import {SamplePage} from "./sample"
import {ThemeProvider} from "./theme"

export const App = () => (
    <ThemeProvider>
        <SamplePage/>
    </ThemeProvider>
)
