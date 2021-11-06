import React from 'react'
import {render} from '@testing-library/react'
import {QueryClient, QueryClientProvider} from "react-query"

const Wrapper: React.FC = ({children}) => {
    const queryClient = new QueryClient()
    queryClient.defaultQueryOptions({retry: false})
    return (
        <QueryClientProvider client={queryClient}>
            {children}
        </QueryClientProvider>
    )
}

const customRender = (ui: React.ReactElement) => render(ui, {wrapper: Wrapper})

export * from '@testing-library/react'
export {customRender as render}
