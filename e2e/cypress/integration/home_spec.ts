describe('Home Page', () => {
  it('should have a greeting', () => {
    cy.visit('/')
    cy.get('main').should('contain', 'With Twocan you can find out what two can do.')
  })
})
