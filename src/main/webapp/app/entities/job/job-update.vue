<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="demoJhVue870App.job.home.createOrEditLabel"
          data-cy="JobCreateUpdateHeading"
          v-text="t$('demoJhVue870App.job.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="job.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="job.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.jobTitle')" for="job-jobTitle"></label>
            <input
              type="text"
              class="form-control"
              name="jobTitle"
              id="job-jobTitle"
              data-cy="jobTitle"
              :class="{ valid: !v$.jobTitle.$invalid, invalid: v$.jobTitle.$invalid }"
              v-model="v$.jobTitle.$model"
              required
            />
            <div v-if="v$.jobTitle.$anyDirty && v$.jobTitle.$invalid">
              <small class="form-text text-danger" v-for="error of v$.jobTitle.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.minSalary')" for="job-minSalary"></label>
            <input
              type="number"
              class="form-control"
              name="minSalary"
              id="job-minSalary"
              data-cy="minSalary"
              :class="{ valid: !v$.minSalary.$invalid, invalid: v$.minSalary.$invalid }"
              v-model.number="v$.minSalary.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.maxSalary')" for="job-maxSalary"></label>
            <input
              type="number"
              class="form-control"
              name="maxSalary"
              id="job-maxSalary"
              data-cy="maxSalary"
              :class="{ valid: !v$.maxSalary.$invalid, invalid: v$.maxSalary.$invalid }"
              v-model.number="v$.maxSalary.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.subSalary')" for="job-subSalary"></label>
            <input
              type="number"
              class="form-control"
              name="subSalary"
              id="job-subSalary"
              data-cy="subSalary"
              :class="{ valid: !v$.subSalary.$invalid, invalid: v$.subSalary.$invalid }"
              v-model.number="v$.subSalary.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.totalSalary')" for="job-totalSalary"></label>
            <input
              type="number"
              class="form-control"
              name="totalSalary"
              id="job-totalSalary"
              data-cy="totalSalary"
              :class="{ valid: !v$.totalSalary.$invalid, invalid: v$.totalSalary.$invalid }"
              v-model.number="v$.totalSalary.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.date')" for="job-date"></label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="job-date"
                  v-model="v$.date.$model"
                  name="date"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="job-date"
                data-cy="date"
                type="text"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                v-model="v$.date.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.codeCode')" for="job-codeCode"></label>
            <input
              type="text"
              class="form-control"
              name="codeCode"
              id="job-codeCode"
              data-cy="codeCode"
              :class="{ valid: !v$.codeCode.$invalid, invalid: v$.codeCode.$invalid }"
              v-model="v$.codeCode.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.profil')" for="job-profil"></label>
            <div>
              <img :src="'data:' + job.profilContentType + ';base64,' + job.profil" style="max-height: 100px" v-if="job.profil" alt="job" />
              <div v-if="job.profil" class="form-text text-danger clearfix">
                <span class="pull-left">{{ job.profilContentType }}, {{ byteSize(job.profil) }}</span>
                <button
                  type="button"
                  @click="clearInputImage('profil', 'profilContentType', 'file_profil')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_profil" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_profil"
                id="file_profil"
                style="display: none"
                data-cy="profil"
                @change="setFileData($event, job, 'profil', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="profil"
              id="job-profil"
              data-cy="profil"
              :class="{ valid: !v$.profil.$invalid, invalid: v$.profil.$invalid }"
              v-model="v$.profil.$model"
              required
            />
            <input type="hidden" class="form-control" name="profilContentType" id="job-profilContentType" v-model="job.profilContentType" />
            <div v-if="v$.profil.$anyDirty && v$.profil.$invalid">
              <small class="form-text text-danger" v-for="error of v$.profil.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label v-text="t$('demoJhVue870App.job.task')" for="job-task"></label>
            <select
              class="form-control"
              id="job-tasks"
              data-cy="task"
              multiple
              name="task"
              v-if="job.tasks !== undefined"
              v-model="job.tasks"
            >
              <option :value="getSelected(job.tasks, taskOption, 'id')" v-for="taskOption in tasks" :key="taskOption.id">
                {{ taskOption.title }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.job.employee')" for="job-employee"></label>
            <select class="form-control" id="job-employee" data-cy="employee" name="employee" v-model="job.employee">
              <option :value="null"></option>
              <option
                :value="job.employee && employeeOption.id === job.employee.id ? job.employee : employeeOption"
                v-for="employeeOption in employees"
                :key="employeeOption.id"
              >
                {{ employeeOption.id }}
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
<script lang="ts" src="./job-update.component.ts"></script>
