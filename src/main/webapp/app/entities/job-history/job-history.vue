<template>
  <div>
    <h2 id="page-heading" data-cy="JobHistoryHeading">
      <span v-text="t$('demoJhVue870App.jobHistory.home.title')" id="job-history-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('demoJhVue870App.jobHistory.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'JobHistoryCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-job-history"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('demoJhVue870App.jobHistory.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && jobHistories && jobHistories.length === 0">
      <span v-text="t$('demoJhVue870App.jobHistory.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="jobHistories && jobHistories.length > 0">
      <table class="table table-striped" aria-describedby="jobHistories">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('startDate')">
              <span v-text="t$('demoJhVue870App.jobHistory.startDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'startDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('endDate')">
              <span v-text="t$('demoJhVue870App.jobHistory.endDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'endDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('language')">
              <span v-text="t$('demoJhVue870App.jobHistory.language')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'language'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('file')">
              <span v-text="t$('demoJhVue870App.jobHistory.file')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'file'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('date')">
              <span v-text="t$('demoJhVue870App.jobHistory.date')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'date'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('duration')">
              <span v-text="t$('demoJhVue870App.jobHistory.duration')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'duration'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('job.id')">
              <span v-text="t$('demoJhVue870App.jobHistory.job')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'job.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('department.id')">
              <span v-text="t$('demoJhVue870App.jobHistory.department')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'department.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('employee.id')">
              <span v-text="t$('demoJhVue870App.jobHistory.employee')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'employee.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="jobHistory in jobHistories" :key="jobHistory.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'JobHistoryView', params: { jobHistoryId: jobHistory.id } }">{{ jobHistory.id }}</router-link>
            </td>
            <td>{{ formatDateShort(jobHistory.startDate) || '' }}</td>
            <td>{{ formatDateShort(jobHistory.endDate) || '' }}</td>
            <td v-text="t$('demoJhVue870App.Language.' + jobHistory.language)"></td>
            <td>
              <a
                v-if="jobHistory.file"
                @click="openFile(jobHistory.fileContentType, jobHistory.file)"
                v-text="t$('entity.action.open')"
              ></a>
              <span v-if="jobHistory.file">{{ jobHistory.fileContentType }}, {{ byteSize(jobHistory.file) }}</span>
            </td>
            <td>{{ formatDateShort(jobHistory.date) || '' }}</td>
            <td>{{ formatDuration(jobHistory.duration) }}</td>
            <td>
              <div v-if="jobHistory.job">
                <router-link :to="{ name: 'JobView', params: { jobId: jobHistory.job.id } }">{{ jobHistory.job.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="jobHistory.department">
                <router-link :to="{ name: 'DepartmentView', params: { departmentId: jobHistory.department.id } }">{{
                  jobHistory.department.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="jobHistory.employee">
                <router-link :to="{ name: 'EmployeeView', params: { employeeId: jobHistory.employee.id } }">{{
                  jobHistory.employee.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'JobHistoryView', params: { jobHistoryId: jobHistory.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'JobHistoryEdit', params: { jobHistoryId: jobHistory.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(jobHistory)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="demoJhVue870App.jobHistory.delete.question"
          data-cy="jobHistoryDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-jobHistory-heading" v-text="t$('demoJhVue870App.jobHistory.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-jobHistory"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeJobHistory()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="jobHistories && jobHistories.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./job-history.component.ts"></script>
