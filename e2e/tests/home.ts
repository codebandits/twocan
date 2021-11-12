import {Selector} from 'testcafe'

fixture`Home`
  .page`http://localhost:3000`

test('home page should have a greeting', async t => {
  await t
    .expect(Selector('main').innerText).contains('With Twocan you can find out what two can do.')
})
