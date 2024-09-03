/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ExpertiseUpdate from './expertise-update.vue';
import ExpertiseService from './expertise.service';
import AlertService from '@/shared/alert/alert.service';

import ExperienceService from '@/entities/experience/experience.service';

type ExpertiseUpdateComponentType = InstanceType<typeof ExpertiseUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const expertiseSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ExpertiseUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Expertise Management Update Component', () => {
    let comp: ExpertiseUpdateComponentType;
    let expertiseServiceStub: SinonStubbedInstance<ExpertiseService>;

    beforeEach(() => {
      route = {};
      expertiseServiceStub = sinon.createStubInstance<ExpertiseService>(ExpertiseService);
      expertiseServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          expertiseService: () => expertiseServiceStub,
          experienceService: () =>
            sinon.createStubInstance<ExperienceService>(ExperienceService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ExpertiseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.expertise = expertiseSample;
        expertiseServiceStub.update.resolves(expertiseSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(expertiseServiceStub.update.calledWith(expertiseSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        expertiseServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ExpertiseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.expertise = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(expertiseServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        expertiseServiceStub.find.resolves(expertiseSample);
        expertiseServiceStub.retrieve.resolves([expertiseSample]);

        // WHEN
        route = {
          params: {
            expertiseId: `${expertiseSample.id}`,
          },
        };
        const wrapper = shallowMount(ExpertiseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.expertise).toMatchObject(expertiseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        expertiseServiceStub.find.resolves(expertiseSample);
        const wrapper = shallowMount(ExpertiseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
