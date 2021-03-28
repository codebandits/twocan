import {render, screen, waitFor, within} from '../test/test-render'
import {App} from '../App'
import {Session} from './session'
import {testServer} from '../test/test-server'
import userEvent from "@testing-library/user-event"
import {MockedEndpoint} from "mockttp"

describe('session', () => {

    let loginEndpoint: MockedEndpoint
    let logoutEndpoint: MockedEndpoint

    describe('when the user is logged out', () => {

        beforeEach(async () => {
            await testServer.get('/api/session').thenJson(200, {data: null})
            await render(<App/>)
        })

        it('should present the homepage', async () => {
            const main = await screen.findByRole('main')
            expect(main).toHaveTextContent('With Twocan you can find out what two can do.')
        })

        it('should display the login option in the menu', async () => {
            const menu = await screen.findByTestId('drawer-menu')
            within(menu).getByRole('button', {name: 'Login'})
        })

        it('should not display the logout option in the menu', async () => {
            const menu = await screen.findByTestId('drawer-menu')
            expect(within(menu).queryByRole('button', {name: 'Logout'})).toBeNull()
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

        it('should not display the login option in the menu', async () => {
            const menu = await screen.findByTestId('drawer-menu')
            expect(within(menu).queryByRole('button', {name: 'Login'})).toBeNull()
        })

        it('should display the logout option in the menu', async () => {
            const menu = await screen.findByTestId('drawer-menu')
            within(menu).getByRole('button', {name: 'Logout'})
        })
    })

    describe('when the user clicks the login option in the menu', () => {
        beforeEach(async () => {
            await testServer.get('/api/session').thenJson(200, {data: null})
            await render(<App/>)
            const menu = await screen.findByTestId('drawer-menu')
            const loginButton = within(menu).getByRole('button', {name: 'Login'})
            userEvent.click(loginButton)
        })

        it('should navigate to the login page', async () => {
            const main = await screen.findByRole('main')
            expect(main).not.toHaveTextContent('With Twocan you can find out what two can do.')
            expect(main).toHaveTextContent('Login')
            within(main).getByLabelText('Email Address')
            within(main).getByRole('button', {name: 'Login'})
        })

        describe('when the user logs in', () => {

            const session = {id: 'session-1', emailAddress: 'bird@example.com'}

            beforeEach(async () => {
                const main = await screen.findByRole('main')
                const emailAddressInput = within(main).getByLabelText('Email Address')
                const submit = within(main).getByRole('button', {name: 'Login'})

                userEvent.type(emailAddressInput, 'bird@example.com')

                loginEndpoint = await testServer.post('/api/login').thenReply(200)
                await testServer.get('/api/session').thenJson(200, {data: session})

                userEvent.click(submit)
            })

            it('should send a login request', async () => {
                await waitFor(async () => {
                    const loginRequests = await loginEndpoint.getSeenRequests()
                    expect(loginRequests).toHaveLength(1)
                    expect(loginRequests[0].body.json).toEqual({emailAddress: 'bird@example.com'})
                })
            })

            it('should navigate to the app', async () => {
                await waitFor(async () => {
                    const main = await screen.findByRole('main')
                    expect(main).not.toHaveTextContent('Login')
                    expect(main).toHaveTextContent('Lorem ipsum dolor sit amet')
                })
            })

            describe('when the user logs out', () => {
                beforeEach(async () => {
                    const logoutButton = await screen.findByRole('button', {name: 'Logout'})
                    logoutEndpoint = await testServer.post('/api/logout').thenReply(200)
                    await testServer.get('/api/session').thenJson(200, {data: null})
                    userEvent.click(logoutButton)
                })

                it('should send a logout request', async () => {
                    await waitFor(async () => {
                        const logoutRequests = await logoutEndpoint.getSeenRequests()
                        expect(logoutRequests).toHaveLength(1)
                    })
                })

                it('should navigate to the homepage', async () => {
                    await waitFor(async () => {
                        const main = await screen.findByRole('main')
                        expect(main).not.toHaveTextContent('Lorem ipsum dolor sit amet')
                        expect(main).toHaveTextContent('With Twocan you can find out what two can do.')
                    })
                })
            })
        })
    })
})
