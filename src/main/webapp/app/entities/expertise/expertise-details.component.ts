import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ExpertiseService from './expertise.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IExpertise } from '@/shared/model/expertise.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ExpertiseDetails',
  setup() {
    const expertiseService = inject('expertiseService', () => new ExpertiseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const expertise: Ref<IExpertise> = ref({});

    const retrieveExpertise = async expertiseId => {
      try {
        const res = await expertiseService().find(expertiseId);
        expertise.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.expertiseId) {
      retrieveExpertise(route.params.expertiseId);
    }

    return {
      alertService,
      expertise,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
