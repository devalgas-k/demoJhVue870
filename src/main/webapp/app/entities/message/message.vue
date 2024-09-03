<template>
  <div>
    <h2 id="page-heading" data-cy="MessageHeading">
      <span v-text="t$('demoJhVue870App.message.home.title')" id="message-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('demoJhVue870App.message.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'MessageCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-message"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('demoJhVue870App.message.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && messages && messages.length === 0">
      <span v-text="t$('demoJhVue870App.message.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="messages && messages.length > 0">
      <table class="table table-striped" aria-describedby="messages">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('name')">
              <span v-text="t$('demoJhVue870App.message.name')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('email')">
              <span v-text="t$('demoJhVue870App.message.email')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'email'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('phone')">
              <span v-text="t$('demoJhVue870App.message.phone')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'phone'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('message')">
              <span v-text="t$('demoJhVue870App.message.message')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'message'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('file')">
              <span v-text="t$('demoJhVue870App.message.file')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'file'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('city')">
              <span v-text="t$('demoJhVue870App.message.city')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'city'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('otherCountry')">
              <span v-text="t$('demoJhVue870App.message.otherCountry')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'otherCountry'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('date')">
              <span v-text="t$('demoJhVue870App.message.date')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'date'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('subject.name')">
              <span v-text="t$('demoJhVue870App.message.subject')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subject.name'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="message in messages" :key="message.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'MessageView', params: { messageId: message.id } }">{{ message.id }}</router-link>
            </td>
            <td>{{ message.name }}</td>
            <td>{{ message.email }}</td>
            <td>{{ message.phone }}</td>
            <td>{{ message.message }}</td>
            <td>
              <a v-if="message.file" @click="openFile(message.fileContentType, message.file)" v-text="t$('entity.action.open')"></a>
              <span v-if="message.file">{{ message.fileContentType }}, {{ byteSize(message.file) }}</span>
            </td>
            <td>{{ message.city }}</td>
            <td>{{ message.otherCountry }}</td>
            <td>{{ formatDateShort(message.date) || '' }}</td>
            <td>
              <div v-if="message.subject">
                <router-link :to="{ name: 'SubjectView', params: { subjectId: message.subject.id } }">{{
                  message.subject.name
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'MessageView', params: { messageId: message.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'MessageEdit', params: { messageId: message.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(message)"
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
        <span id="demoJhVue870App.message.delete.question" data-cy="messageDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-message-heading" v-text="t$('demoJhVue870App.message.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-message"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeMessage()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="messages && messages.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./message.component.ts"></script>
