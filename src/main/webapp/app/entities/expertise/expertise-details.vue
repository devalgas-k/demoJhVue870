<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="expertise">
        <h2 class="jh-entity-heading" data-cy="expertiseDetailsHeading">
          <span v-text="t$('demoJhVue870App.expertise.detail.title')"></span> {{ expertise.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('demoJhVue870App.expertise.title')"></span>
          </dt>
          <dd>
            <span>{{ expertise.title }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.expertise.description')"></span>
          </dt>
          <dd>
            <span>{{ expertise.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.expertise.level')"></span>
          </dt>
          <dd>
            <span>{{ expertise.level }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.expertise.experience')"></span>
          </dt>
          <dd>
            <span v-for="(experience, i) in expertise.experiences" :key="experience.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ExperienceView', params: { experienceId: experience.id } }">{{ experience.company }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="expertise.id"
          :to="{ name: 'ExpertiseEdit', params: { expertiseId: expertise.id } }"
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

<script lang="ts" src="./expertise-details.component.ts"></script>
