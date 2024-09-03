/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ExpertiseDetails from './expertise-details.vue';
import ExpertiseService from './expertise.service';
import AlertService from '@/shared/alert/alert.service';

type ExpertiseDetailsComponentType = InstanceType<typeof ExpertiseDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const expertiseSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Expertise Management Detail Component', () => {
    let expertiseServiceStub: SinonStubbedInstance<ExpertiseService>;
    let mountOptions: MountingOptions<ExpertiseDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      expertiseServiceStub = sinon.createStubInstance<ExpertiseService>(ExpertiseService);

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
          expertiseService: () => expertiseServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        expertiseServiceStub.find.resolves(expertiseSample);
        route = {
          params: {
            expertiseId: `${123}`,
          },
        };
        const wrapper = shallowMount(ExpertiseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.expertise).toMatchObject(expertiseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        expertiseServiceStub.find.resolves(expertiseSample);
        const wrapper = shallowMount(ExpertiseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
