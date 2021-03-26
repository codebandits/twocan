import React from 'react'
import {SWRConfig} from 'swr'
import {render} from '@testing-library/react'

const Wrapper: React.FC = ({children}) => (
    <SWRConfig value={{dedupingInterval: 0}}>
        {children}
    </SWRConfig>
)
const customRender = (ui: React.ReactElement) => render(ui, {wrapper: Wrapper})

export * from '@testing-library/react'
export {customRender as render}
