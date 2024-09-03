<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="demoJhVue870App.jobHistory.home.createOrEditLabel"
          data-cy="JobHistoryCreateUpdateHeading"
          v-text="t$('demoJhVue870App.jobHistory.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="jobHistory.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="jobHistory.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.startDate')" for="job-history-startDate"></label>
            <div class="d-flex">
              <input
                id="job-history-startDate"
                data-cy="startDate"
                type="datetime-local"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                :value="convertDateTimeFromServer(v$.startDate.$model)"
                @change="updateInstantField('startDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.endDate')" for="job-history-endDate"></label>
            <div class="d-flex">
              <input
                id="job-history-endDate"
                data-cy="endDate"
                type="datetime-local"
                class="form-control"
                name="endDate"
                :class="{ valid: !v$.endDate.$invalid, invalid: v$.endDate.$invalid }"
                :value="convertDateTimeFromServer(v$.endDate.$model)"
                @change="updateInstantField('endDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.language')" for="job-history-language"></label>
            <select
              class="form-control"
              name="language"
              :class="{ valid: !v$.language.$invalid, invalid: v$.language.$invalid }"
              v-model="v$.language.$model"
              id="job-history-language"
              data-cy="language"
            >
              <option
                v-for="language in languageValues"
                :key="language"
                :value="language"
                :label="t$('demoJhVue870App.Language.' + language)"
              >
                {{ language }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.file')" for="job-history-file"></label>
            <div>
              <div v-if="jobHistory.file" class="form-text text-danger clearfix">
                <a class="pull-left" @click="openFile(jobHistory.fileContentType, jobHistory.file)" v-text="t$('entity.action.open')"></a
                ><br />
                <span class="pull-left">{{ jobHistory.fileContentType }}, {{ byteSize(jobHistory.file) }}</span>
                <button
                  type="button"
                  @click="
                    jobHistory.file = null;
                    jobHistory.fileContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_file" v-text="t$('entity.action.addblob')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_file"
                id="file_file"
                style="display: none"
                data-cy="file"
                @change="setFileData($event, jobHistory, 'file', false)"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="file"
              id="job-history-file"
              data-cy="file"
              :class="{ valid: !v$.file.$invalid, invalid: v$.file.$invalid }"
              v-model="v$.file.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="fileContentType"
              id="job-history-fileContentType"
              v-model="jobHistory.fileContentType"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.date')" for="job-history-date"></label>
            <div class="d-flex">
              <input
                id="job-history-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                :value="convertDateTimeFromServer(v$.date.$model)"
                @change="updateZonedDateTimeField('date', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.duration')" for="job-history-duration"></label>
            <input
              type="text"
              class="form-control"
              name="duration"
              id="job-history-duration"
              data-cy="duration"
              :class="{ valid: !v$.duration.$invalid, invalid: v$.duration.$invalid }"
              v-model="v$.duration.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.job')" for="job-history-job"></label>
            <select class="form-control" id="job-history-job" data-cy="job" name="job" v-model="jobHistory.job">
              <option :value="null"></option>
              <option
                :value="jobHistory.job && jobOption.id === jobHistory.job.id ? jobHistory.job : jobOption"
                v-for="jobOption in jobs"
                :key="jobOption.id"
              >
                {{ jobOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.department')" for="job-history-department"></label>
            <select class="form-control" id="job-history-department" data-cy="department" name="department" v-model="jobHistory.department">
              <option :value="null"></option>
              <option
                :value="
                  jobHistory.department && departmentOption.id === jobHistory.department.id ? jobHistory.department : departmentOption
                "
                v-for="departmentOption in departments"
                :key="departmentOption.id"
              >
                {{ departmentOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.jobHistory.employee')" for="job-history-employee"></label>
            <select class="form-control" id="job-history-employee" data-cy="employee" name="employee" v-model="jobHistory.employee">
              <option :value="null"></option>
              <option
                :value="jobHistory.employee && employeeOption.id === jobHistory.employee.id ? jobHistory.employee : employeeOption"
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
<script lang="ts" src="./job-history-update.component.ts"></script>
