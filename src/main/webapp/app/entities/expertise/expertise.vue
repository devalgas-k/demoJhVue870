<template>
  <div>
    <h2 id="page-heading" data-cy="ExpertiseHeading">
      <span v-text="t$('demoJhVue870App.expertise.home.title')" id="expertise-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('demoJhVue870App.expertise.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ExpertiseCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-expertise"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('demoJhVue870App.expertise.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && expertise && expertise.length === 0">
      <span v-text="t$('demoJhVue870App.expertise.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="expertise && expertise.length > 0">
      <table class="table table-striped" aria-describedby="expertise">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('title')">
              <span v-text="t$('demoJhVue870App.expertise.title')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'title'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('description')">
              <span v-text="t$('demoJhVue870App.expertise.description')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('level')">
              <span v-text="t$('demoJhVue870App.expertise.level')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'level'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="expertise in expertise" :key="expertise.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ExpertiseView', params: { expertiseId: expertise.id } }">{{ expertise.id }}</router-link>
            </td>
            <td>{{ expertise.title }}</td>
            <td>{{ expertise.description }}</td>
            <td>{{ expertise.level }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ExpertiseView', params: { expertiseId: expertise.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ExpertiseEdit', params: { expertiseId: expertise.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(expertise)"
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
          id="demoJhVue870App.expertise.delete.question"
          data-cy="expertiseDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-expertise-heading" v-text="t$('demoJhVue870App.expertise.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-expertise"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeExpertise()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="expertise && expertise.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./expertise.component.ts"></script>
