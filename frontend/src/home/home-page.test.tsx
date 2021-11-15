import {render, screen} from '@testing-library/react'
import {HomePage} from './home-page'

describe('Home Page', () => {
  it('should have a greeting', () => {
    render(<HomePage/>)
    screen.getByText('With Twocan you can find out what two can do.', {exact: false})
  })
})
