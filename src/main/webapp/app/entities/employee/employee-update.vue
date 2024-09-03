<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="demoJhVue870App.employee.home.createOrEditLabel"
          data-cy="EmployeeCreateUpdateHeading"
          v-text="t$('demoJhVue870App.employee.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="employee.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="employee.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.firstName')" for="employee-firstName"></label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="employee-firstName"
              data-cy="firstName"
              :class="{ valid: !v$.firstName.$invalid, invalid: v$.firstName.$invalid }"
              v-model="v$.firstName.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.lastName')" for="employee-lastName"></label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="employee-lastName"
              data-cy="lastName"
              :class="{ valid: !v$.lastName.$invalid, invalid: v$.lastName.$invalid }"
              v-model="v$.lastName.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.email')" for="employee-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="employee-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
              required
            />
            <div v-if="v$.email.$anyDirty && v$.email.$invalid">
              <small class="form-text text-danger" v-for="error of v$.email.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.phoneNumber')" for="employee-phoneNumber"></label>
            <input
              type="text"
              class="form-control"
              name="phoneNumber"
              id="employee-phoneNumber"
              data-cy="phoneNumber"
              :class="{ valid: !v$.phoneNumber.$invalid, invalid: v$.phoneNumber.$invalid }"
              v-model="v$.phoneNumber.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.hireDate')" for="employee-hireDate"></label>
            <div class="d-flex">
              <input
                id="employee-hireDate"
                data-cy="hireDate"
                type="datetime-local"
                class="form-control"
                name="hireDate"
                :class="{ valid: !v$.hireDate.$invalid, invalid: v$.hireDate.$invalid }"
                :value="convertDateTimeFromServer(v$.hireDate.$model)"
                @change="updateInstantField('hireDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.salary')" for="employee-salary"></label>
            <input
              type="number"
              class="form-control"
              name="salary"
              id="employee-salary"
              data-cy="salary"
              :class="{ valid: !v$.salary.$invalid, invalid: v$.salary.$invalid }"
              v-model.number="v$.salary.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.commissionPct')" for="employee-commissionPct"></label>
            <input
              type="number"
              class="form-control"
              name="commissionPct"
              id="employee-commissionPct"
              data-cy="commissionPct"
              :class="{ valid: !v$.commissionPct.$invalid, invalid: v$.commissionPct.$invalid }"
              v-model.number="v$.commissionPct.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.level')" for="employee-level"></label>
            <input
              type="number"
              class="form-control"
              name="level"
              id="employee-level"
              data-cy="level"
              :class="{ valid: !v$.level.$invalid, invalid: v$.level.$invalid }"
              v-model.number="v$.level.$model"
            />
            <div v-if="v$.level.$anyDirty && v$.level.$invalid">
              <small class="form-text text-danger" v-for="error of v$.level.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.contract')" for="employee-contract"></label>
            <select
              class="form-control"
              name="contract"
              :class="{ valid: !v$.contract.$invalid, invalid: v$.contract.$invalid }"
              v-model="v$.contract.$model"
              id="employee-contract"
              data-cy="contract"
            >
              <option
                v-for="contract in contractValues"
                :key="contract"
                :value="contract"
                :label="t$('demoJhVue870App.Contract.' + contract)"
              >
                {{ contract }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.cv')" for="employee-cv"></label>
            <div>
              <div v-if="employee.cv" class="form-text text-danger clearfix">
                <a class="pull-left" @click="openFile(employee.cvContentType, employee.cv)" v-text="t$('entity.action.open')"></a><br />
                <span class="pull-left">{{ employee.cvContentType }}, {{ byteSize(employee.cv) }}</span>
                <button
                  type="button"
                  @click="
                    employee.cv = null;
                    employee.cvContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_cv" v-text="t$('entity.action.addblob')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_cv"
                id="file_cv"
                style="display: none"
                data-cy="cv"
                @change="setFileData($event, employee, 'cv', false)"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="cv"
              id="employee-cv"
              data-cy="cv"
              :class="{ valid: !v$.cv.$invalid, invalid: v$.cv.$invalid }"
              v-model="v$.cv.$model"
            />
            <input type="hidden" class="form-control" name="cvContentType" id="employee-cvContentType" v-model="employee.cvContentType" />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.manager')" for="employee-manager"></label>
            <select class="form-control" id="employee-manager" data-cy="manager" name="manager" v-model="employee.manager">
              <option :value="null"></option>
              <option
                :value="employee.manager && employeeOption.id === employee.manager.id ? employee.manager : employeeOption"
                v-for="employeeOption in employees"
                :key="employeeOption.id"
              >
                {{ employeeOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.employee.department')" for="employee-department"></label>
            <select class="form-control" id="employee-department" data-cy="department" name="department" v-model="employee.department">
              <option :value="null"></option>
              <option
                :value="employee.department && departmentOption.id === employee.department.id ? employee.department : departmentOption"
                v-for="departmentOption in departments"
                :key="departmentOption.id"
              >
                {{ departmentOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./employee-update.component.ts"></script>
