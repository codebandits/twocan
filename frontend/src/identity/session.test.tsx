import {render, screen, waitFor, within} from '../test/test-render'
import {App} from '../App'
import {Session} from './session'
import {testServer} from '../test/test-server'
import userEvent from "@testing-library/user-event"
import {MockedEndpoint} from "mockttp"

describe('session', () => {

    let loginEndpoint: MockedEndpoint
    let logoutEndpoint: MockedEndpoint
    let getSessionEndpoint: MockedEndpoint

    describe('when the user is logged out', () => {

        beforeEach(async () => {
            getSessionEndpoint = await testServer.get('/api/session').thenJson(200, {data: null})
            await render(<App/>)
        })

        it('should present the option to login', async () => {
            const main = await screen.findByRole('main')
            within(main).getByLabelText('Email Address')
            within(main).getByRole('button', {name: 'Login'})
        })

        it('should not the session menu button', async () => {
            expect(screen.queryByTestId('session-menu-button')).toBeNull()
        })

        describe('when the user logs in', () => {

            const session = {id: 'session-1', emailAddress: 'bird@example.com'}

            beforeEach(async () => {
                const main = await screen.findByRole('main')
                const emailAddressInput = within(main).getByLabelText('Email Address')
                const submit = within(main).getByRole('button', {name: 'Login'})

                userEvent.type(emailAddressInput, 'bird@example.com')

                loginEndpoint = await testServer.post('/api/login').thenReply(200)
                getSessionEndpoint = await testServer.get('/api/session').thenJson(200, {data: session})

                userEvent.click(submit)
            })

            it('should send a login request', async () => {
                await waitFor(async () => {
                    const loginRequests = await loginEndpoint.getSeenRequests()
                    expect(loginRequests).toHaveLength(1)
                    expect(loginRequests[0].body.json).toEqual({emailAddress: 'bird@example.com'})
                })
            })

            it('should display the session menu button', async () => {
                await waitFor(async () => {
                    screen.getByTestId('session-menu-button')
                })
            })

            describe('when the user logs out', () => {
                beforeEach(async () => {
                    const sessionMenuButton = await screen.findByTestId('session-menu-button')
                    userEvent.click(sessionMenuButton)
                    const sessionMenu = screen.getByTestId('session-menu')
                    const logoutButton = within(sessionMenu).getByRole('menuitem', {name: 'Logout'})

                    logoutEndpoint = await testServer.post('/api/logout').thenReply(200)
                    getSessionEndpoint = await testServer.get('/api/session').thenJson(200, {data: null})
                    userEvent.click(logoutButton)
                })

                it('should send a logout request', async () => {
                    await waitFor(async () => {
                        const logoutRequests = await logoutEndpoint.getSeenRequests()
                        expect(logoutRequests).toHaveLength(1)
                    })
                })

                it('should not the session menu button', async () => {
                    await waitFor(async () => {
                        expect(screen.queryByTestId('session-menu-button')).toBeNull()
                    })
                })
            })
        })
    })

    describe('when the user is logged in', () => {

        const session: Session = {
            userId: 'user-1',
            name: 'Toucan Sam',
        }

        beforeEach(async () => {
            await testServer.get('/api/session').thenJson(200, {data: session})
            await render(<App/>)
        })

        it('should not present the option to login', async () => {
            const main = await screen.findByRole('main')
            expect(within(main).queryByLabelText('Email Address')).toBeNull()
            expect(within(main).queryByRole('button', {name: 'Login'})).toBeNull()
        })

        it('should display the session menu button', async () => {
            await screen.findByTestId('session-menu-button')
        })
    })
})
