<template>
  <div>
    <h2 id="page-heading" data-cy="ExperienceHeading">
      <span v-text="t$('demoJhVue870App.experience.home.title')" id="experience-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('demoJhVue870App.experience.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ExperienceCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-experience"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('demoJhVue870App.experience.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && experiences && experiences.length === 0">
      <span v-text="t$('demoJhVue870App.experience.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="experiences && experiences.length > 0">
      <table class="table table-striped" aria-describedby="experiences">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('title')">
              <span v-text="t$('demoJhVue870App.experience.title')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'title'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('company')">
              <span v-text="t$('demoJhVue870App.experience.company')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'company'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('description')">
              <span v-text="t$('demoJhVue870App.experience.description')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('logoCompany')">
              <span v-text="t$('demoJhVue870App.experience.logoCompany')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'logoCompany'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('inProgress')">
              <span v-text="t$('demoJhVue870App.experience.inProgress')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'inProgress'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('contract')">
              <span v-text="t$('demoJhVue870App.experience.contract')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'contract'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('startDate')">
              <span v-text="t$('demoJhVue870App.experience.startDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'startDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('endDate')">
              <span v-text="t$('demoJhVue870App.experience.endDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'endDate'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="experience in experiences" :key="experience.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ExperienceView', params: { experienceId: experience.id } }">{{ experience.id }}</router-link>
            </td>
            <td>{{ experience.title }}</td>
            <td>{{ experience.company }}</td>
            <td>{{ experience.description }}</td>
            <td>
              <a v-if="experience.logoCompany" @click="openFile(experience.logoCompanyContentType, experience.logoCompany)">
                <img
                  :src="'data:' + experience.logoCompanyContentType + ';base64,' + experience.logoCompany"
                  style="max-height: 30px"
                  alt="experience"
                />
              </a>
              <span v-if="experience.logoCompany">{{ experience.logoCompanyContentType }}, {{ byteSize(experience.logoCompany) }}</span>
            </td>
            <td>{{ experience.inProgress }}</td>
            <td v-text="t$('demoJhVue870App.Contract.' + experience.contract)"></td>
            <td>{{ experience.startDate }}</td>
            <td>{{ experience.endDate }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ExperienceView', params: { experienceId: experience.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ExperienceEdit', params: { experienceId: experience.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(experience)"
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
          id="demoJhVue870App.experience.delete.question"
          data-cy="experienceDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-experience-heading" v-text="t$('demoJhVue870App.experience.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-experience"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeExperience()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="experiences && experiences.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./experience.component.ts"></script>
