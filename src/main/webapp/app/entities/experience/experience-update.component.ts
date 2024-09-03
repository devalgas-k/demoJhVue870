import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ExperienceService from './experience.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ExpertiseService from '@/entities/expertise/expertise.service';
import { type IExpertise } from '@/shared/model/expertise.model';
import { Experience, type IExperience } from '@/shared/model/experience.model';
import { Contract } from '@/shared/model/enumerations/contract.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ExperienceUpdate',
  setup() {
    const experienceService = inject('experienceService', () => new ExperienceService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const experience: Ref<IExperience> = ref(new Experience());

    const expertiseService = inject('expertiseService', () => new ExpertiseService());

    const expertise: Ref<IExpertise[]> = ref([]);
    const contractValues: Ref<string[]> = ref(Object.keys(Contract));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      expertiseService()
        .retrieve()
        .then(res => {
          expertise.value = res.data;
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
      company: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      description: {},
      logoCompany: {},
      inProgress: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      contract: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      startDate: {},
      endDate: {},
      expertise: {},
    };
    const v$ = useVuelidate(validationRules, experience as any);
    v$.value.$validate();

    return {
      experienceService,
      alertService,
      experience,
      previousState,
      contractValues,
      isSaving,
      currentLanguage,
      expertise,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {
    this.experience.expertise = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.experience.id) {
        this.experienceService()
          .update(this.experience)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('demoJhVue870App.experience.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.experienceService()
          .create(this.experience)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('demoJhVue870App.experience.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.experience && field && fieldContentType) {
        if (Object.hasOwn(this.experience, field)) {
          this.experience[field] = null;
        }
        if (Object.hasOwn(this.experience, fieldContentType)) {
          this.experience[fieldContentType] = null;
        }
        if (idInput) {
          (<any>this).$refs[idInput] = null;
        }
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
