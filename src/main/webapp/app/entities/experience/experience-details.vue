<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="experience">
        <h2 class="jh-entity-heading" data-cy="experienceDetailsHeading">
          <span v-text="t$('demoJhVue870App.experience.detail.title')"></span> {{ experience.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('demoJhVue870App.experience.title')"></span>
          </dt>
          <dd>
            <span>{{ experience.title }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.company')"></span>
          </dt>
          <dd>
            <span>{{ experience.company }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.description')"></span>
          </dt>
          <dd>
            <span>{{ experience.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.logoCompany')"></span>
          </dt>
          <dd>
            <div v-if="experience.logoCompany">
              <a @click="openFile(experience.logoCompanyContentType, experience.logoCompany)">
                <img
                  :src="'data:' + experience.logoCompanyContentType + ';base64,' + experience.logoCompany"
                  style="max-width: 100%"
                  alt="experience"
                />
              </a>
              {{ experience.logoCompanyContentType }}, {{ byteSize(experience.logoCompany) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.inProgress')"></span>
          </dt>
          <dd>
            <span>{{ experience.inProgress }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.contract')"></span>
          </dt>
          <dd>
            <span v-text="t$('demoJhVue870App.Contract.' + experience.contract)"></span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.startDate')"></span>
          </dt>
          <dd>
            <span>{{ experience.startDate }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.endDate')"></span>
          </dt>
          <dd>
            <span>{{ experience.endDate }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.experience.expertise')"></span>
          </dt>
          <dd>
            <span v-for="(expertise, i) in experience.expertise" :key="expertise.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ExpertiseView', params: { expertiseId: expertise.id } }">{{ expertise.title }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="experience.id"
          :to="{ name: 'ExperienceEdit', params: { experienceId: experience.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./experience-details.component.ts"></script>
