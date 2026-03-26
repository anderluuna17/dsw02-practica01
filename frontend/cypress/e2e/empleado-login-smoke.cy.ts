describe('Empleado login smoke', () => {
  it('logs in with active empleado and returns EMPLEADO actor profile', () => {
    const unique = Date.now();
    const correo = `empleado.smoke.${unique}@empresa.com`;
    const contrasena = 'SmokePass123';

    cy.request({
      method: 'POST',
      url: '/api/v1/empleados',
      auth: { username: 'admin', password: 'admin123' },
      body: {
        nombre: `Empleado Smoke ${unique}`,
        direccion: 'Calle Smoke 1',
        telefono: '5558889999',
        correo,
        contrasena,
      },
    });

    cy.intercept('GET', '**/api/v1/empleados/auth/me').as('authMe');

    cy.visit('/empleado/login');

    cy.get('[data-cy="empleado-correo"]').clear().type(correo);
    cy.get('[data-cy="empleado-password"]').clear().type(contrasena);
    cy.get('[data-cy="empleado-login-submit"]').click();

    cy.wait('@authMe').then((interception) => {
      expect(interception.response?.statusCode).to.eq(200);
      expect(interception.response?.body?.actorType).to.eq('EMPLEADO');
      expect(interception.response?.body?.permissions).to.deep.equal(['SELF']);
    });

    cy.window().then((win: any) => {
      const empleadoLoginRoot = win.document.querySelector('app-empleado-login-container');
      const component = win.ng.getComponent(empleadoLoginRoot);
      win.ng.applyChanges(component);
    });

    cy.get('[data-cy="empleado-login-success"]').should('be.visible');

    cy.window().then((win) => {
      expect(win.localStorage.length).to.eq(0);
      expect(win.sessionStorage.length).to.eq(0);
    });
  });

  it('shows generic error for invalid password', () => {
    const unique = Date.now();
    const correo = `empleado.smoke.invalid.${unique}@empresa.com`;
    const contrasena = 'SmokePass123';

    cy.request({
      method: 'POST',
      url: '/api/v1/empleados',
      auth: { username: 'admin', password: 'admin123' },
      body: {
        nombre: `Empleado Smoke Invalid ${unique}`,
        direccion: 'Calle Smoke 2',
        telefono: '5557771111',
        correo,
        contrasena,
      },
    });

    cy.visit('/empleado/login');

    cy.get('[data-cy="empleado-correo"]').clear().type(correo);
    cy.get('[data-cy="empleado-password"]').clear().type('PasswordIncorrecta');
    cy.get('[data-cy="empleado-login-submit"]').click();

    cy.get('[data-cy="empleado-login-error"]').should('contain.text', 'Credenciales invalidas');
  });

  it('shows same generic error for inactive account', () => {
    const unique = Date.now();
    const correo = `empleado.smoke.inactivo.${unique}@empresa.com`;
    const contrasena = 'SmokePass123';

    cy.request({
      method: 'POST',
      url: '/api/v1/empleados',
      auth: { username: 'admin', password: 'admin123' },
      body: {
        nombre: `Empleado Smoke Inactivo ${unique}`,
        direccion: 'Calle Smoke 3',
        telefono: '5556662222',
        correo,
        contrasena,
      },
    }).then((createRes) => {
      const clave = createRes.body?.clave as string;

      cy.request({
        method: 'PATCH',
        url: `/api/v1/empleados/${clave}/estado`,
        auth: { username: 'admin', password: 'admin123' },
        body: { activo: false },
      });
    });

    cy.visit('/empleado/login');

    cy.get('[data-cy="empleado-correo"]').clear().type(correo);
    cy.get('[data-cy="empleado-password"]').clear().type(contrasena);
    cy.get('[data-cy="empleado-login-submit"]').click();

    cy.get('[data-cy="empleado-login-error"]').should('contain.text', 'Credenciales invalidas');
  });
});
