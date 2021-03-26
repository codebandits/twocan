import {render, screen, within} from '../test/test-render'
import {App} from '../App'
import {Session} from './session'
import {testServer} from '../test/test-server'

describe('Login', () => {

    let banner: HTMLElement
    let main: HTMLElement

    const renderWithSession = async (session: any) => {
        await testServer.get('/api/session').thenJson(200, {data: session})
        render(<App/>)
        banner = await screen.findByRole('banner')
        main = await screen.findByRole('main')
    }

    describe('when the user is logged out', () => {

        beforeEach(async () => {
            await renderWithSession(null)
        })

        it('should present the option to login', () => {
            expect(main).toHaveTextContent('Login')
        })

        it('should present the option to register', () => {
            expect(main).toHaveTextContent('Register')
        })

        it('should not the session icon', () => {
            expect(within(banner).queryByTestId('session-menu')).toBeNull()
        })
    })

    describe('when the user is logged in', () => {

        const session: Session = {
            userId: 'user-1',
            name: 'Toucan Sam',
        }

        beforeEach(async () => {
            await renderWithSession(session)
        })

        it('should not present the option to login', () => {
            expect(main).not.toHaveTextContent('Login')
        })

        it('should present the option to register', () => {
            expect(main).not.toHaveTextContent('Register')
        })

        it('should display the session icon', () => {
            within(banner).getByTestId('session-menu')
        })
    })
})
