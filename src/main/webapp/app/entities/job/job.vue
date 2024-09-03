<template>
  <div>
    <h2 id="page-heading" data-cy="JobHeading">
      <span v-text="t$('demoJhVue870App.job.home.title')" id="job-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('demoJhVue870App.job.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'JobCreate' }" custom v-slot="{ navigate }">
          <button @click="navigate" id="jh-create-entity" data-cy="entityCreateButton" class="btn btn-primary jh-create-entity create-job">
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('demoJhVue870App.job.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && jobs && jobs.length === 0">
      <span v-text="t$('demoJhVue870App.job.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="jobs && jobs.length > 0">
      <table class="table table-striped" aria-describedby="jobs">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('jobTitle')">
              <span v-text="t$('demoJhVue870App.job.jobTitle')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'jobTitle'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('minSalary')">
              <span v-text="t$('demoJhVue870App.job.minSalary')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'minSalary'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('maxSalary')">
              <span v-text="t$('demoJhVue870App.job.maxSalary')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'maxSalary'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('subSalary')">
              <span v-text="t$('demoJhVue870App.job.subSalary')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subSalary'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('totalSalary')">
              <span v-text="t$('demoJhVue870App.job.totalSalary')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'totalSalary'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('date')">
              <span v-text="t$('demoJhVue870App.job.date')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'date'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('codeCode')">
              <span v-text="t$('demoJhVue870App.job.codeCode')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'codeCode'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('profil')">
              <span v-text="t$('demoJhVue870App.job.profil')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'profil'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('employee.id')">
              <span v-text="t$('demoJhVue870App.job.employee')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'employee.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="job in jobs" :key="job.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'JobView', params: { jobId: job.id } }">{{ job.id }}</router-link>
            </td>
            <td>{{ job.jobTitle }}</td>
            <td>{{ job.minSalary }}</td>
            <td>{{ job.maxSalary }}</td>
            <td>{{ job.subSalary }}</td>
            <td>{{ job.totalSalary }}</td>
            <td>{{ job.date }}</td>
            <td>{{ job.codeCode }}</td>
            <td>
              <a v-if="job.profil" @click="openFile(job.profilContentType, job.profil)">
                <img :src="'data:' + job.profilContentType + ';base64,' + job.profil" style="max-height: 30px" alt="job" />
              </a>
              <span v-if="job.profil">{{ job.profilContentType }}, {{ byteSize(job.profil) }}</span>
            </td>
            <td>
              <div v-if="job.employee">
                <router-link :to="{ name: 'EmployeeView', params: { employeeId: job.employee.id } }">{{ job.employee.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'JobView', params: { jobId: job.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'JobEdit', params: { jobId: job.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(job)"
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
        <span id="demoJhVue870App.job.delete.question" data-cy="jobDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-job-heading" v-text="t$('demoJhVue870App.job.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-job"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeJob()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="jobs && jobs.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./job.component.ts"></script>
