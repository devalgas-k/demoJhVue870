import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import EmployeeService from './employee.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import DepartmentService from '@/entities/department/department.service';
import { type IDepartment } from '@/shared/model/department.model';
import { Employee, type IEmployee } from '@/shared/model/employee.model';
import { Contract } from '@/shared/model/enumerations/contract.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'EmployeeUpdate',
  setup() {
    const employeeService = inject('employeeService', () => new EmployeeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const employee: Ref<IEmployee> = ref(new Employee());

    const employees: Ref<IEmployee[]> = ref([]);

    const departmentService = inject('departmentService', () => new DepartmentService());

    const departments: Ref<IDepartment[]> = ref([]);
    const contractValues: Ref<string[]> = ref(Object.keys(Contract));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'fr'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveEmployee = async employeeId => {
      try {
        const res = await employeeService().find(employeeId);
        res.hireDate = new Date(res.hireDate);
        employee.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.employeeId) {
      retrieveEmployee(route.params.employeeId);
    }

    const initRelationships = () => {
      employeeService()
        .retrieve()
        .then(res => {
          employees.value = res.data;
        });
      departmentService()
        .retrieve()
        .then(res => {
          departments.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      firstName: {},
      lastName: {},
      email: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      phoneNumber: {},
      hireDate: {},
      salary: {},
      commissionPct: {},
      level: {
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
        max: validations.maxValue(t$('entity.validation.max', { max: 14 }).toString(), 14),
      },
      contract: {},
      cv: {},
      manager: {},
      department: {},
    };
    const v$ = useVuelidate(validationRules, employee as any);
    v$.value.$validate();

    return {
      employeeService,
      alertService,
      employee,
      previousState,
      contractValues,
      isSaving,
      currentLanguage,
      employees,
      departments,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: employee }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.employee.id) {
        this.employeeService()
          .update(this.employee)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('demoJhVue870App.employee.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.employeeService()
          .create(this.employee)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('demoJhVue870App.employee.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
