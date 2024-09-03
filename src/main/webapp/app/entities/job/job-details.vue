<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="job">
        <h2 class="jh-entity-heading" data-cy="jobDetailsHeading">
          <span v-text="t$('demoJhVue870App.job.detail.title')"></span> {{ job.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('demoJhVue870App.job.jobTitle')"></span>
          </dt>
          <dd>
            <span>{{ job.jobTitle }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.minSalary')"></span>
          </dt>
          <dd>
            <span>{{ job.minSalary }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.maxSalary')"></span>
          </dt>
          <dd>
            <span>{{ job.maxSalary }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.subSalary')"></span>
          </dt>
          <dd>
            <span>{{ job.subSalary }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.totalSalary')"></span>
          </dt>
          <dd>
            <span>{{ job.totalSalary }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.date')"></span>
          </dt>
          <dd>
            <span>{{ job.date }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.codeCode')"></span>
          </dt>
          <dd>
            <span>{{ job.codeCode }}</span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.profil')"></span>
          </dt>
          <dd>
            <div v-if="job.profil">
              <a @click="openFile(job.profilContentType, job.profil)">
                <img :src="'data:' + job.profilContentType + ';base64,' + job.profil" style="max-width: 100%" alt="job" />
              </a>
              {{ job.profilContentType }}, {{ byteSize(job.profil) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.task')"></span>
          </dt>
          <dd>
            <span v-for="(task, i) in job.tasks" :key="task.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'TaskView', params: { taskId: task.id } }">{{ task.title }}</router-link>
            </span>
          </dd>
          <dt>
            <span v-text="t$('demoJhVue870App.job.employee')"></span>
          </dt>
          <dd>
            <div v-if="job.employee">
              <router-link :to="{ name: 'EmployeeView', params: { employeeId: job.employee.id } }">{{ job.employee.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="job.id" :to="{ name: 'JobEdit', params: { jobId: job.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./job-details.component.ts"></script>
