import {Session} from "../identity/session";
import {testServer} from "../test/test-server";
import {render, screen, waitFor, waitForElementToBeRemoved, within} from "../test/test-render";
import {App} from "../App";
import {Bird} from "./bird";
import {assertDefined} from "../test/test-assert";
import userEvent from "@testing-library/user-event";
import {MockedEndpoint} from "mockttp";

describe('birds', () => {
    describe('when a user is logged in', () => {
        const session: Session = {
            id: 'session-1',
            user: {
                id: 'user-1',
                firstName: 'Toucan',
                lastName: 'Sam',
            }
        }

        const birds: Bird[] = [
            {id: 'bird-1', firstName: 'Mark', lastName: 'Twain'},
            {id: 'bird-2', firstName: 'Tom', lastName: 'Sawyer'},
            {id: 'bird-3', firstName: 'Huck', lastName: 'Finn'},
        ]

        beforeEach(async () => {
            await testServer.get('/api/session').thenJson(200, {data: session})
            await testServer.get('/api/birds').thenJson(200, {data: birds})
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

        describe('when the user creates a new bird', () => {
            let addBirdEndpoint: MockedEndpoint

            beforeEach(async () => {
                const main = await screen.findByRole('main')
                const addButton = within(main).getByRole('button', {name: 'Add'})
                userEvent.click(addButton)
                const dialog = await screen.findByRole('dialog')
                userEvent.type(within(dialog).getByLabelText('First Name'), 'Samuel')
                userEvent.type(within(dialog).getByLabelText('Last Name'), 'Clemens')

                addBirdEndpoint = await testServer.post('/api/birds').thenReply(200)
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
    })
})
