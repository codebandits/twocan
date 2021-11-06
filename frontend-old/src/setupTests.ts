// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom'
import {testServer} from './test/test-server'
import {suppressJsdomHttpErrors} from './test/test-suppress-errors'
import {setLogger} from 'react-query'
import mediaQuery from 'css-mediaquery'
import {queryClient} from './App'

suppressJsdomHttpErrors()

setLogger({
    log: () => undefined,
    warn: () => undefined,
    error: () => undefined,
})

beforeEach(async () => {
    await testServer.start(80)
    queryClient.clear()
})

afterEach(async () => {
    await testServer.stop()
})

beforeAll(() => {
    Object.defineProperty(window, 'matchMedia', {
        value: (query: string) => ({
            matches: mediaQuery.match(query, {width: window.innerWidth}),
            addListener: () => {
            },
            removeListener: () => {
            },
        }),
    })
})
