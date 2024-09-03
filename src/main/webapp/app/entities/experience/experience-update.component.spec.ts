/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ExperienceUpdate from './experience-update.vue';
import ExperienceService from './experience.service';
import AlertService from '@/shared/alert/alert.service';

import ExpertiseService from '@/entities/expertise/expertise.service';

type ExperienceUpdateComponentType = InstanceType<typeof ExperienceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const experienceSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ExperienceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Experience Management Update Component', () => {
    let comp: ExperienceUpdateComponentType;
    let experienceServiceStub: SinonStubbedInstance<ExperienceService>;

    beforeEach(() => {
      route = {};
      experienceServiceStub = sinon.createStubInstance<ExperienceService>(ExperienceService);
      experienceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          experienceService: () => experienceServiceStub,
          expertiseService: () =>
            sinon.createStubInstance<ExpertiseService>(ExpertiseService, {
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
        const wrapper = shallowMount(ExperienceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.experience = experienceSample;
        experienceServiceStub.update.resolves(experienceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(experienceServiceStub.update.calledWith(experienceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        experienceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ExperienceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.experience = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(experienceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        experienceServiceStub.find.resolves(experienceSample);
        experienceServiceStub.retrieve.resolves([experienceSample]);

        // WHEN
        route = {
          params: {
            experienceId: `${experienceSample.id}`,
          },
        };
        const wrapper = shallowMount(ExperienceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.experience).toMatchObject(experienceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        experienceServiceStub.find.resolves(experienceSample);
        const wrapper = shallowMount(ExperienceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
