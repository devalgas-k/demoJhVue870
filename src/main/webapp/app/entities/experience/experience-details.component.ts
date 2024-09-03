import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ExperienceService from './experience.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IExperience } from '@/shared/model/experience.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ExperienceDetails',
  setup() {
    const experienceService = inject('experienceService', () => new ExperienceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const experience: Ref<IExperience> = ref({});

    const retrieveExperience = async experienceId => {
      try {
        const res = await experienceService().find(experienceId);
        experience.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.experienceId) {
      retrieveExperience(route.params.experienceId);
    }

    return {
      alertService,
      experience,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
