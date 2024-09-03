/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ExperienceDetails from './experience-details.vue';
import ExperienceService from './experience.service';
import AlertService from '@/shared/alert/alert.service';

type ExperienceDetailsComponentType = InstanceType<typeof ExperienceDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const experienceSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Experience Management Detail Component', () => {
    let experienceServiceStub: SinonStubbedInstance<ExperienceService>;
    let mountOptions: MountingOptions<ExperienceDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      experienceServiceStub = sinon.createStubInstance<ExperienceService>(ExperienceService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          experienceService: () => experienceServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        experienceServiceStub.find.resolves(experienceSample);
        route = {
          params: {
            experienceId: `${123}`,
          },
        };
        const wrapper = shallowMount(ExperienceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.experience).toMatchObject(experienceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        experienceServiceStub.find.resolves(experienceSample);
        const wrapper = shallowMount(ExperienceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
