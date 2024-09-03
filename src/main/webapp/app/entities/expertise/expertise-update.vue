<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="demoJhVue870App.expertise.home.createOrEditLabel"
          data-cy="ExpertiseCreateUpdateHeading"
          v-text="t$('demoJhVue870App.expertise.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="expertise.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="expertise.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.expertise.title')" for="expertise-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="expertise-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.expertise.description')" for="expertise-description"></label>
            <textarea
              class="form-control"
              name="description"
              id="expertise-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.expertise.level')" for="expertise-level"></label>
            <input
              type="number"
              class="form-control"
              name="level"
              id="expertise-level"
              data-cy="level"
              :class="{ valid: !v$.level.$invalid, invalid: v$.level.$invalid }"
              v-model.number="v$.level.$model"
            />
            <div v-if="v$.level.$anyDirty && v$.level.$invalid">
              <small class="form-text text-danger" v-for="error of v$.level.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label v-text="t$('demoJhVue870App.expertise.experience')" for="expertise-experience"></label>
            <select
              class="form-control"
              id="expertise-experiences"
              data-cy="experience"
              multiple
              name="experience"
              v-if="expertise.experiences !== undefined"
              v-model="expertise.experiences"
            >
              <option
                :value="getSelected(expertise.experiences, experienceOption, 'id')"
                v-for="experienceOption in experiences"
                :key="experienceOption.id"
              >
                {{ experienceOption.company }}
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
<script lang="ts" src="./expertise-update.component.ts"></script>
