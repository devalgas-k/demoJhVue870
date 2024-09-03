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

describe('Job e2e test', () => {
  const jobPageUrl = '/job';
  const jobPageUrlPattern = new RegExp('/job(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const jobSample = {
    jobTitle: 'Executif de la sécurité international',
    profil: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    profilContentType: 'unknown',
  };

  let job;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/jobs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/jobs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/jobs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (job) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/jobs/${job.id}`,
      }).then(() => {
        job = undefined;
      });
    }
  });

  it('Jobs menu should load Jobs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('job');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Job').should('exist');
    cy.url().should('match', jobPageUrlPattern);
  });

  describe('Job page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(jobPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Job page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/job/new$'));
        cy.getEntityCreateUpdateHeading('Job');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', jobPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/jobs',
          body: jobSample,
        }).then(({ body }) => {
          job = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/jobs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/jobs?page=0&size=20>; rel="last",<http://localhost/api/jobs?page=0&size=20>; rel="first"',
              },
              body: [job],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(jobPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Job page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('job');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', jobPageUrlPattern);
      });

      it('edit button click should load edit Job page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Job');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', jobPageUrlPattern);
      });

      it('edit button click should load edit Job page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Job');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', jobPageUrlPattern);
      });

      it('last delete button click should delete instance of Job', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('job').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', jobPageUrlPattern);

        job = undefined;
      });
    });
  });

  describe('new Job page', () => {
    beforeEach(() => {
      cy.visit(`${jobPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Job');
    });

    it('should create an instance of Job', () => {
      cy.get(`[data-cy="jobTitle"]`).type("Architecte de l'assurance futur");
      cy.get(`[data-cy="jobTitle"]`).should('have.value', "Architecte de l'assurance futur");

      cy.get(`[data-cy="minSalary"]`).type('990.48');
      cy.get(`[data-cy="minSalary"]`).should('have.value', '990.48');

      cy.get(`[data-cy="maxSalary"]`).type('14290');
      cy.get(`[data-cy="maxSalary"]`).should('have.value', '14290');

      cy.get(`[data-cy="subSalary"]`).type('21219.02');
      cy.get(`[data-cy="subSalary"]`).should('have.value', '21219.02');

      cy.get(`[data-cy="totalSalary"]`).type('14694.55');
      cy.get(`[data-cy="totalSalary"]`).should('have.value', '14694.55');

      cy.get(`[data-cy="date"]`).type('2024-09-02');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2024-09-02');

      cy.get(`[data-cy="codeCode"]`).type('cd18aeef-0a95-49e8-9596-cdd8afb35698');
      cy.get(`[data-cy="codeCode"]`).invoke('val').should('match', new RegExp('cd18aeef-0a95-49e8-9596-cdd8afb35698'));

      cy.setFieldImageAsBytesOfEntity('profil', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        job = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', jobPageUrlPattern);
    });
  });
});
