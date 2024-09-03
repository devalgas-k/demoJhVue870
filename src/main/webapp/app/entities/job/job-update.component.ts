import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import JobService from './job.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import TaskService from '@/entities/task/task.service';
import { type ITask } from '@/shared/model/task.model';
import EmployeeService from '@/entities/employee/employee.service';
import { type IEmployee } from '@/shared/model/employee.model';
import { type IJob, Job } from '@/shared/model/job.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'JobUpdate',
  setup() {
    const jobService = inject('jobService', () => new JobService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const job: Ref<IJob> = ref(new Job());

    const taskService = inject('taskService', () => new TaskService());

    const tasks: Ref<ITask[]> = ref([]);

    const employeeService = inject('employeeService', () => new EmployeeService());

    const employees: Ref<IEmployee[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveJob = async jobId => {
      try {
        const res = await jobService().find(jobId);
        job.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.jobId) {
      retrieveJob(route.params.jobId);
    }

    const initRelationships = () => {
      taskService()
        .retrieve()
        .then(res => {
          tasks.value = res.data;
        });
      employeeService()
        .retrieve()
        .then(res => {
          employees.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      jobTitle: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      minSalary: {},
      maxSalary: {},
      subSalary: {},
      totalSalary: {},
      date: {},
      codeCode: {},
      profil: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      tasks: {},
      employee: {},
    };
    const v$ = useVuelidate(validationRules, job as any);
    v$.value.$validate();

    return {
      jobService,
      alertService,
      job,
      previousState,
      isSaving,
      currentLanguage,
      tasks,
      employees,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {
    this.job.tasks = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.job.id) {
        this.jobService()
          .update(this.job)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('demoJhVue870App.job.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.jobService()
          .create(this.job)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('demoJhVue870App.job.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.job && field && fieldContentType) {
        if (Object.hasOwn(this.job, field)) {
          this.job[field] = null;
        }
        if (Object.hasOwn(this.job, fieldContentType)) {
          this.job[fieldContentType] = null;
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
