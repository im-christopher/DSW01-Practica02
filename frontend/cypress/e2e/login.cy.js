describe('Login e2e', () => {
  const empleadosPageFixture = 'empleados-page.json';

  it('permite entrar con credenciales validas', () => {
    cy.intercept('GET', '**/api/v1/empleados?*', {
      statusCode: 200,
      fixture: empleadosPageFixture
    }).as('listarEmpleados');

    cy.intercept({
      method: 'GET',
      pathname: '/api/v1/empleados',
      query: {
        page: '0',
        size: '1'
      }
    }, {
      statusCode: 200,
      fixture: empleadosPageFixture
    }).as('probeAccess');

    cy.visit('/login');
    cy.get('#username').clear().type('admin');
    cy.get('#password').clear().type('admin123');
    cy.contains('button', 'Entrar').click();

    cy.wait('@probeAccess');
    cy.wait('@listarEmpleados');
    cy.url().should('include', '/empleados');
    cy.contains('h1', 'Gestion de empleados').should('be.visible');
  });

  it('muestra mensaje de error con credenciales invalidas', () => {
    cy.intercept({
      method: 'GET',
      pathname: '/api/v1/empleados',
      query: {
        page: '0',
        size: '1'
      }
    }, {
      statusCode: 401,
      body: {
        code: 'UNAUTHORIZED',
        message: 'Credenciales invalidas'
      }
    }).as('probeAccessUnauthorized');

    cy.visit('/login');
    cy.get('#username').clear().type('admin');
    cy.get('#password').clear().type('incorrecta');
    cy.contains('button', 'Entrar').click();

    cy.wait('@probeAccessUnauthorized');
    cy.contains('Credenciales invalidas. Verifica usuario o contrasena.').should('be.visible');
    cy.url().should('include', '/login');
  });
});
