// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom'
import {testServer} from './test/test-server'
import {suppressJsdomHttpErrors} from './test/test-suppress-errors'
import {setLogger} from 'react-query'
import mediaQuery from 'css-mediaquery'

suppressJsdomHttpErrors()

setLogger({
    log: () => undefined,
    warn: () => undefined,
    error: () => undefined,
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

beforeEach(async () => {
    await testServer.start(80)
})

afterEach(async () => {
    await testServer.stop()
})
