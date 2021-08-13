import {Session} from "../identity/session";
import {testServer} from "../test/test-server";
import {render, screen, waitFor, waitForElementToBeRemoved, within} from "../test/test-render";
import {App} from "../App";
import {Bird} from "./bird";
import {assertDefined} from "../test/test-assert";
import userEvent from "@testing-library/user-event";
import {MockedEndpoint} from "mockttp";

describe('birds', () => {
    describe('when a user with birds is logged in', () => {
        const session: Session = {
            id: 'session-1',
            user: {
                id: 'user-1',
                firstName: 'Toucan',
                lastName: 'Sam',
            }
        }

        const birds: Bird[] = [
            {id: 'bird-1', firstName: 'Mark', lastName: 'Twain', lastFlight: null},
            {id: 'bird-2', firstName: 'Tom', lastName: 'Sawyer', lastFlight: null},
            {id: 'bird-3', firstName: 'Huck', lastName: 'Finn', lastFlight: null},
        ]

        beforeEach(async () => {
            await testServer.get('/api/session').thenJson(200, {status: 'OK', data: session})
            await testServer.get('/api/birds').thenJson(200, {status: 'OK', data: birds})
            await render(<App/>)
        })

        it('should display the birds page', async () => {
            const main = await screen.findByRole('main')
            within(main).getByRole('heading', {name: 'Birds'})
        })

        it('should display each bird', async () => {
            const main = await screen.findByRole('main')
            assertDefined(within(main).getByText('Mark Twain').closest<HTMLElement>('li'))
            assertDefined(within(main).getByText('Tom Sawyer').closest<HTMLElement>('li'))
            assertDefined(within(main).getByText('Huck Finn').closest<HTMLElement>('li'))
        })

        describe('when the user adds a new flight for a bird', () => {
            let addFlightEndpoint: MockedEndpoint

            beforeEach(async () => {
                const main = await screen.findByRole('main')
                const birdItem = assertDefined(within(main).getByText('Mark Twain').closest<HTMLElement>('li'))
                const flightButton = within(birdItem).getByRole('button', {name: 'flight'})

                addFlightEndpoint = await testServer.post('/api/flights').thenJson(200, {status: 'CREATED', id: '88'})
                userEvent.click(flightButton)
            })

            it('should send an add flight request', async () => {
                await waitFor(async () => {
                    const addFlightRequests = await addFlightEndpoint.getSeenRequests()
                    expect(addFlightRequests).toHaveLength(1)
                    expect(addFlightRequests[0].body.json).toEqual({
                        birdId: 'bird-1',
                    })
                })
            })
        })

        describe('when the user creates a new bird', () => {
            let addBirdEndpoint: MockedEndpoint

            beforeEach(async () => {
                const main = await screen.findByRole('main')
                const addButton = within(main).getByRole('button', {name: 'Add'})
                userEvent.click(addButton)
                const dialog = await screen.findByRole('dialog')
                userEvent.type(within(dialog).getByLabelText('First Name'), 'Samuel')
                userEvent.type(within(dialog).getByLabelText('Last Name'), 'Clemens')

                addBirdEndpoint = await testServer.post('/api/birds').thenJson(200, {status: 'CREATED', id: '99'})
                userEvent.click(within(dialog).getByRole('button', {name: 'Add'}))
                await waitForElementToBeRemoved(() => screen.queryByRole('dialog'))
            })

            it('should send an add bird request', async () => {
                await waitFor(async () => {
                    const addBirdRequests = await addBirdEndpoint.getSeenRequests()
                    expect(addBirdRequests).toHaveLength(1)
                    expect(addBirdRequests[0].body.json).toEqual({
                        firstName: 'Samuel',
                        lastName: 'Clemens',
                    })
                })
            })

            it('should reset the add bird dialog form', async () => {
                const main = screen.getByRole('main')
                const addButton = within(main).getByRole('button', {name: 'Add'})
                userEvent.click(addButton)
                const dialog = await screen.findByRole('dialog')
                expect(within(dialog).getByLabelText('First Name')).toHaveValue('')
                expect(within(dialog).getByLabelText('Last Name')).toHaveValue('')
            })
        })

        describe('when the user receives errors creating a new bird', () => {
            let addBirdEndpoint: MockedEndpoint

            beforeEach(async () => {
                const main = await screen.findByRole('main')
                const addButton = within(main).getByRole('button', {name: 'Add'})
                userEvent.click(addButton)
                const dialog = await screen.findByRole('dialog')
                userEvent.type(within(dialog).getByLabelText('First Name'), 'Samuel')
                userEvent.type(within(dialog).getByLabelText('Last Name'), 'Clemens')

                const errors = {firstName: 'bad first name', lastName: 'bad last name'}
                addBirdEndpoint = await testServer.post('/api/birds').thenJson(400, {
                    status: 'BAD_REQUEST',
                    errors: errors
                })
                userEvent.click(within(dialog).getByRole('button', {name: 'Add'}))
                await waitFor(async () => {
                    const addBirdRequests = await addBirdEndpoint.getSeenRequests()
                    expect(addBirdRequests).toHaveLength(1)
                })
            })

            it('should not reset the add bird dialog form', async () => {
                const dialog = await screen.findByRole('dialog')
                expect(within(dialog).getByLabelText('First Name')).toHaveValue('Samuel')
                expect(within(dialog).getByLabelText('Last Name')).toHaveValue('Clemens')
            })

            it('should display the errors', async () => {
                await waitFor(async () => {
                    const dialog = await screen.findByRole('dialog')
                    expect(within(dialog).getByTestId('firstName-helper-text')).toHaveTextContent('bad first name')
                    expect(within(dialog).getByTestId('lastName-helper-text')).toHaveTextContent('bad last name')
                })
            })
        })
    })

    describe('when a user with no birds is logged', () => {
        const session: Session = {
            id: 'session-1',
            user: {
                id: 'user-1',
                firstName: 'Toucan',
                lastName: 'Sam',
            }
        }

        const birds: Bird[] = []

        beforeEach(async () => {
            await testServer.get('/api/session').thenJson(200, {status: 'OK', data: session})
            await testServer.get('/api/birds').thenJson(200, {status: 'OK', data: birds})
            await render(<App/>)
        })

        it('should display the birds page', async () => {
            const main = await screen.findByRole('main')
            within(main).getByRole('heading', {name: 'Birds'})
        })

        it('should display not display a list', async () => {
            const main = await screen.findByRole('main')
            expect(within(main).queryByRole('list')).toBeNull()
        })

        it('should suggest the user adds a bird', async () => {
            const main = await screen.findByRole('main')
            within(main).getByText('Don\'t you know any birds? Jump out of the nest and add them!')
        })
    })
})
