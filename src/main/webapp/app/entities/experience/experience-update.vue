<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="demoJhVue870App.experience.home.createOrEditLabel"
          data-cy="ExperienceCreateUpdateHeading"
          v-text="t$('demoJhVue870App.experience.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="experience.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="experience.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.title')" for="experience-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="experience-title"
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
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.company')" for="experience-company"></label>
            <input
              type="text"
              class="form-control"
              name="company"
              id="experience-company"
              data-cy="company"
              :class="{ valid: !v$.company.$invalid, invalid: v$.company.$invalid }"
              v-model="v$.company.$model"
              required
            />
            <div v-if="v$.company.$anyDirty && v$.company.$invalid">
              <small class="form-text text-danger" v-for="error of v$.company.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.description')" for="experience-description"></label>
            <textarea
              class="form-control"
              name="description"
              id="experience-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.logoCompany')" for="experience-logoCompany"></label>
            <div>
              <img
                :src="'data:' + experience.logoCompanyContentType + ';base64,' + experience.logoCompany"
                style="max-height: 100px"
                v-if="experience.logoCompany"
                alt="experience"
              />
              <div v-if="experience.logoCompany" class="form-text text-danger clearfix">
                <span class="pull-left">{{ experience.logoCompanyContentType }}, {{ byteSize(experience.logoCompany) }}</span>
                <button
                  type="button"
                  @click="clearInputImage('logoCompany', 'logoCompanyContentType', 'file_logoCompany')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_logoCompany" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_logoCompany"
                id="file_logoCompany"
                style="display: none"
                data-cy="logoCompany"
                @change="setFileData($event, experience, 'logoCompany', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="logoCompany"
              id="experience-logoCompany"
              data-cy="logoCompany"
              :class="{ valid: !v$.logoCompany.$invalid, invalid: v$.logoCompany.$invalid }"
              v-model="v$.logoCompany.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="logoCompanyContentType"
              id="experience-logoCompanyContentType"
              v-model="experience.logoCompanyContentType"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.inProgress')" for="experience-inProgress"></label>
            <input
              type="checkbox"
              class="form-check"
              name="inProgress"
              id="experience-inProgress"
              data-cy="inProgress"
              :class="{ valid: !v$.inProgress.$invalid, invalid: v$.inProgress.$invalid }"
              v-model="v$.inProgress.$model"
              required
            />
            <div v-if="v$.inProgress.$anyDirty && v$.inProgress.$invalid">
              <small class="form-text text-danger" v-for="error of v$.inProgress.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.contract')" for="experience-contract"></label>
            <select
              class="form-control"
              name="contract"
              :class="{ valid: !v$.contract.$invalid, invalid: v$.contract.$invalid }"
              v-model="v$.contract.$model"
              id="experience-contract"
              data-cy="contract"
              required
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
            <div v-if="v$.contract.$anyDirty && v$.contract.$invalid">
              <small class="form-text text-danger" v-for="error of v$.contract.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.startDate')" for="experience-startDate"></label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="experience-startDate"
                  v-model="v$.startDate.$model"
                  name="startDate"
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
                id="experience-startDate"
                data-cy="startDate"
                type="text"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                v-model="v$.startDate.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('demoJhVue870App.experience.endDate')" for="experience-endDate"></label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="experience-endDate"
                  v-model="v$.endDate.$model"
                  name="endDate"
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
                id="experience-endDate"
                data-cy="endDate"
                type="text"
                class="form-control"
                name="endDate"
                :class="{ valid: !v$.endDate.$invalid, invalid: v$.endDate.$invalid }"
                v-model="v$.endDate.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label v-text="t$('demoJhVue870App.experience.expertise')" for="experience-expertise"></label>
            <select
              class="form-control"
              id="experience-expertise"
              data-cy="expertise"
              multiple
              name="expertise"
              v-if="experience.expertise !== undefined"
              v-model="experience.expertise"
            >
              <option
                :value="getSelected(experience.expertise, expertiseOption, 'id')"
                v-for="expertiseOption in expertise"
                :key="expertiseOption.id"
              >
                {{ expertiseOption.title }}
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
<script lang="ts" src="./experience-update.component.ts"></script>
