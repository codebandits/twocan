// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom'
import {cache} from 'swr'
import {testServer} from './test/test-server'

beforeEach(async () => {
    await testServer.start(80)
})

afterEach(async () => {
    await testServer.stop()
    cache.clear()
})
