import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Message e2e test', () => {
  const messagePageUrl = '/message';
  const messagePageUrlPattern = new RegExp('/message(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const messageSample = { name: 'foule', email: '~#,F*P@hc=L.C' };

  let message;
  let subject;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/subjects',
      body: { name: 'maintenir ouin' },
    }).then(({ body }) => {
      subject = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/messages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/messages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/messages/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/subjects', {
      statusCode: 200,
      body: [subject],
    });
  });

  afterEach(() => {
    if (message) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/messages/${message.id}`,
      }).then(() => {
        message = undefined;
      });
    }
  });

  afterEach(() => {
    if (subject) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/subjects/${subject.id}`,
      }).then(() => {
        subject = undefined;
      });
    }
  });

  it('Messages menu should load Messages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('message');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Message').should('exist');
    cy.url().should('match', messagePageUrlPattern);
  });

  describe('Message page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(messagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Message page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/message/new$'));
        cy.getEntityCreateUpdateHeading('Message');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/messages',
          body: {
            ...messageSample,
            subject,
          },
        }).then(({ body }) => {
          message = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/messages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/messages?page=0&size=20>; rel="last",<http://localhost/api/messages?page=0&size=20>; rel="first"',
              },
              body: [message],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(messagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Message page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('message');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });

      it('edit button click should load edit Message page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Message');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });

      it('edit button click should load edit Message page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Message');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });

      it('last delete button click should delete instance of Message', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('message').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);

        message = undefined;
      });
    });
  });

  describe('new Message page', () => {
    beforeEach(() => {
      cy.visit(`${messagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Message');
    });

    it('should create an instance of Message', () => {
      cy.get(`[data-cy="name"]`).type('actionnaire');
      cy.get(`[data-cy="name"]`).should('have.value', 'actionnaire');

      cy.get(`[data-cy="email"]`).type('6p{?L@I9.(,`');
      cy.get(`[data-cy="email"]`).should('have.value', '6p{?L@I9.(,`');

      cy.get(`[data-cy="phone"]`).type('6.(05475 2235933');
      cy.get(`[data-cy="phone"]`).should('have.value', '6.(05475 2235933');

      cy.get(`[data-cy="message"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="message"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.setFieldImageAsBytesOfEntity('file', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="city"]`).type('Poitiers');
      cy.get(`[data-cy="city"]`).should('have.value', 'Poitiers');

      cy.get(`[data-cy="otherCountry"]`).type('hisser');
      cy.get(`[data-cy="otherCountry"]`).should('have.value', 'hisser');

      cy.get(`[data-cy="date"]`).type('2024-09-02T15:16');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2024-09-02T15:16');

      cy.get(`[data-cy="subject"]`).select(1);

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        message = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', messagePageUrlPattern);
    });
  });
});
