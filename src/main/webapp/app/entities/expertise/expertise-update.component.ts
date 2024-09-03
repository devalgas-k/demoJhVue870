import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ExpertiseService from './expertise.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ExperienceService from '@/entities/experience/experience.service';
import { type IExperience } from '@/shared/model/experience.model';
import { Expertise, type IExpertise } from '@/shared/model/expertise.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ExpertiseUpdate',
  setup() {
    const expertiseService = inject('expertiseService', () => new ExpertiseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const expertise: Ref<IExpertise> = ref(new Expertise());

    const experienceService = inject('experienceService', () => new ExperienceService());

    const experiences: Ref<IExperience[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      experienceService()
        .retrieve()
        .then(res => {
          experiences.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      description: {},
      level: {
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 20 }).toString(), 20),
        max: validations.maxValue(t$('entity.validation.max', { max: 100 }).toString(), 100),
      },
      experiences: {},
    };
    const v$ = useVuelidate(validationRules, expertise as any);
    v$.value.$validate();

    return {
      expertiseService,
      alertService,
      expertise,
      previousState,
      isSaving,
      currentLanguage,
      experiences,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {
    this.expertise.experiences = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.expertise.id) {
        this.expertiseService()
          .update(this.expertise)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('demoJhVue870App.expertise.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.expertiseService()
          .create(this.expertise)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('demoJhVue870App.expertise.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
